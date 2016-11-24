package org.jfantasy.member.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.autoconfigure.ApiGatewaySettings;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.httpclient.HttpClientUtil;
import org.jfantasy.framework.httpclient.Request;
import org.jfantasy.framework.httpclient.Response;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.member.bean.Member;
import org.jfantasy.member.bean.Wallet;
import org.jfantasy.member.dao.MemberDao;
import org.jfantasy.member.dao.WalletDao;
import org.jfantasy.oauth.userdetails.enums.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WalletService {

    private static final Log LOG = LogFactory.getLog(WalletService.class);

    @Autowired
    private WalletDao walletDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private ApiGatewaySettings apiGatewaySettings;

    private Wallet loadByAccount(String accountNo) {
        Wallet wallet = this.walletDao.findUnique(Restrictions.eq("account", accountNo));
        if (wallet != null) {
            return wallet;
        }
        try {
            Response response = HttpClientUtil.doGet(apiGatewaySettings.getUrl() + "/accounts/" + accountNo);
            if (response.getStatusCode() != 200) {
                throw new ValidationException(203.1f, "检查账号出错");
            }
            JsonNode account = response.json();
            String owner = account.get("owner").asText();
            String type = account.get("type").asText();
            BigDecimal amount = account.get("amount").decimalValue();
            if (!type.equals("platform")) {
                String[] asr = owner.split(":");
                String username = asr[1];
                return newWallet(memberDao.findUnique(Restrictions.eq("username", username)), accountNo, amount);
            } else {
                return newWallet(null, accountNo, amount);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new ValidationException(203.3f, "网络问题!");
        }
    }

    /**
     * 创建 用户钱包
     *
     * @param member  用户
     * @param account 资金账号
     * @param amount  资金余额(应该与账号一致)
     * @return Wallet
     */
    private Wallet newWallet(Member member, String account, BigDecimal amount) {
        Wallet wallet = new Wallet();
        wallet.setMember(member);
        wallet.setAccount(account);
        wallet.setAmount(amount);
        wallet.setIncome(BigDecimal.ZERO);
        //初始化积分与成长值
        wallet.setGrowth(0L);
        wallet.setPoints(0L);
        //初始化账单 并 计算收益
        return walletDao.insert(wallet);
    }

    @Transactional
    private Wallet save(Member member) {
        Wallet wallet = this.walletDao.findUnique(Restrictions.eq("member", member));
        if (wallet == null) {
            String owner = Scope.member + ":" + member.getUsername();
            //1、检查账号信息
            try {
                Response response = HttpClientUtil.doGet(apiGatewaySettings.getUrl() + "/accounts?EQS_owner=" + owner);
                if (response.getStatusCode() != 200) {
                    throw new ValidationException(203.1f, "检查账号出错");
                }
                JsonNode node = response.json().get("items");
                JsonNode account = node.get(0);
                if (account == null) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "personal");
                    data.put("owner", owner);
                    Request request = new Request(new StringEntity(JSON.serialize(data), ContentType.APPLICATION_JSON));
                    response = HttpClientUtil.doPost(apiGatewaySettings.getUrl() + "/accounts", request);
                    if (response.getStatusCode() != 201) {
                        throw new ValidationException(203.2f, "创建账号出错");
                    }
                    account = response.json();
                }
                return newWallet(member, account.get("sn").asText(), BigDecimal.valueOf(account.get("amount").asDouble()));
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                throw new ValidationException(203.3f, "网络问题!");
            }
        }
        return wallet;
    }

    @Transactional
    private Wallet getWalletByOwner(String owner) {
        String[] asr = owner.split(":");
        String username = asr[1];
        switch (asr[0]) {
            case "member":
                return this.save(memberDao.findUnique(Restrictions.eq("username", username)));
            default:
        }
        return null;
    }

    @Transactional
    public Wallet getWalletByMember(Long memberId) {
        return this.save(memberDao.get(memberId));
    }

    public Wallet getWallet(Long id) {
        return this.walletDao.findUnique(Restrictions.eq("id", id));
    }

    public Pager<Wallet> findPager(Pager<Wallet> pager, List<PropertyFilter> filters) {
        return this.walletDao.findPager(pager, filters);
    }

    @Transactional
    public Wallet saveOrUpdateWallet(JsonNode account) {
        String accountSn = account.get("sn").asText();
        BigDecimal accountAmount = account.get("amount").decimalValue();
        String accountType = account.get("type").asText();
        String accountOwner = account.get("owner").asText();

        Wallet wallet = this.walletDao.findUnique(Restrictions.eq("account", accountSn));
        if (wallet == null) {
            switch (accountType) {
                case "personal":
                    String username = accountOwner.replaceAll("^[^:]+:", "");
                    Member member = memberDao.findUnique(Restrictions.eq("username", username));
                    if (member == null) {
                        throw new RestException("用户不存在!");
                    }
                    return newWallet(member, accountSn, accountAmount);
                case "platform":
                case "enterprise":
                    return newWallet(null, accountSn, accountAmount);
                default:
            }
        } else {
            return updateWallet(wallet.getId(), accountAmount);
        }
        throw new RestException("创建账号失败!");
    }

    private Wallet updateWallet(Long id, BigDecimal amount) {
        Wallet wallet = this.walletDao.get(id);
        wallet.setAmount(amount);
        this.walletDao.update(wallet);
        return wallet;
    }

    @Transactional
    public void addCard(String account, Map<String, Object> data) {
        // 关联卡
        Wallet wallet = this.loadByAccount(account);
        if (wallet == null) {
            LOG.error("绑卡时，账号：" + account + "未发现");
            return;
        }
        // 计算附加服务
        if (!data.isEmpty()) {
            //添加成长值
            if (data.containsKey("growth")) {
                if (wallet.getGrowth() == null) {
                    wallet.setGrowth(0L);
                }
                wallet.setGrowth(wallet.getGrowth() + Long.valueOf(data.get("growth").toString()));
            }
            //添加积分
            if (data.containsKey("point")) {
                if (wallet.getPoints() == null) {
                    wallet.setPoints(0L);
                }
                wallet.setPoints(wallet.getPoints() + Long.valueOf(data.get("point").toString()));
            }
        }
        wallet.setCards(ObjectUtil.defaultValue(wallet.getCards(), 0L) + 1);
        this.walletDao.update(wallet);
    }

}
