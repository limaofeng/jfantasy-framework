package org.jfantasy.trade.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.autoconfigure.ApiGatewaySettings;
import org.jfantasy.card.bean.Card;
import org.jfantasy.card.dao.CardDao;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.order.bean.ExtraService;
import org.jfantasy.trade.bean.*;
import org.jfantasy.trade.bean.enums.*;
import org.jfantasy.trade.dao.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private static Log LOG = LogFactory.getLog(AccountService.class);

    private final AccountDao accountDao;
    private final ProjectDao projectDao;
    private final TransactionDao transactionDao;
    private final PointDao pointDao;
    private final BillDao billDao;
    private final CardDao cardDao;
    private ApiGatewaySettings apiGatewaySettings;
    private PasswordEncoder passwordEncoder = new StandardPasswordEncoder();

    @Autowired
    public AccountService(PointDao pointDao, ProjectDao projectDao, TransactionDao transactionDao, BillDao billDao, AccountDao accountDao, CardDao cardDao) {
        this.pointDao = pointDao;
        this.projectDao = projectDao;
        this.transactionDao = transactionDao;
        this.billDao = billDao;
        this.accountDao = accountDao;
        this.cardDao = cardDao;
    }

    @Transactional
    public Pager<Account> findPager(Pager<Account> pager, List<PropertyFilter> filters) {
        return this.accountDao.findPager(pager, filters);
    }

    public Account findUniqueByOwner(String owner) {
        Account account = this.accountDao.findUnique(Restrictions.eq("owner", owner));
        if (account != null) {
            return account;
        }
        return save(AccountType.personal, owner, null);
    }

    @Transactional
    public Account findUnique(AccountType type, String owner) {
        return this.accountDao.findUnique(Restrictions.eq("type", type), Restrictions.eq("owner", owner));
    }

    public Account get(String id) {
        return this.accountDao.get(id);
    }

    /**
     * 创建账户
     *
     * @param type  账户类型
     * @param owner 所有者
     * @return Account
     */
    @Transactional
    public Account save(AccountType type, String owner, String password) {
        if (this.accountDao.count(Restrictions.eq("type", type), Restrictions.eq("owner", owner)) > 0) {
            throw new ValidationException(103.1f, "账号存在,创建失败");
        }
        Account account = new Account();
        account.setType(type);
        account.setAmount(BigDecimal.ZERO);
        account.setPoints(0L);
        account.setOwner(owner);
        account.setStatus(StringUtil.isBlank(password) ? AccountStatus.unactivated : AccountStatus.activated);

        //加载 owner 详细信息
        if (AccountType.enterprise == account.getType() || AccountType.personal == account.getType()) {
            ownerByMember(account);
        }

        return this.accountDao.save(account);
    }

    @Transactional
    public void update(Account account) {
        Account old = this.accountDao.get(account.getSn());
        old.setOwnerId(account.getOwnerId());
        old.setOwnerType(account.getOwnerType());
        old.setOwnerName(account.getOwnerName());
        this.accountDao.update(old);
    }

    private void ownerByMember(Account account) {
        try {
            HttpResponse<JsonNode> postResponse = Unirest.get(apiGatewaySettings.getUrl() + "/members/" + account.getOwner()).asJson();
            JsonNode jsonNode = postResponse.getBody();

            JSONObject member = jsonNode.getObject();
            Long memberId = member.getLong("id");
            String memberType = member.getString("type");
            String targetType = member.has("target_type") ? member.getString("target_type") : null;
            String targetId = member.has("target_id") ? member.getString("target_id") : null;
            String name = member.getString("nick_name");
            if ("personal".equals(memberType)) {//个人
                account.setOwnerId(memberId.toString());
                account.setOwnerType("personal");
                account.setOwnerName(name);
            } else if ("doctor".equals(memberType)) {//医生
                account.setOwnerId(targetId);
                account.setOwnerType(targetType);
                account.setOwnerName(name);
            } else if (ObjectUtil.exists(new String[]{"company", "pharmacy", "clinic"}, memberType)) {//集团
                account.setOwnerId(targetId);
                account.setOwnerType(memberType);
                account.setOwnerName(name);
            }
        } catch (UnirestException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * 划账接口
     *
     * @param trxNo    交易单号
     * @param password 如果为 internal 内部付款需要提供支付密码
     * @param notes    描述
     * @return Transaction
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction transfer(String trxNo, String password, String notes) {
        Transaction transaction = transactionDao.get(trxNo);
        /*
        if (transaction.getChannel() == TxChannel.internal) {
            Account from = this.accountDao.get(transaction.getFrom());
            if (from.getStatus() != AccountStatus.activated) {
                throw new RestException("账户未激活不能进行付款操作");
            }
            if (StringUtil.isBlank(password)) {
                throw new RestException("支付密码不能为空");
            }
            if (!passwordEncoder.matches(from.getPassword(), password)) {
                throw new RestException("支付密码错误");
            }
        }
        */
        return this.transfer(trxNo, notes);
    }

    /**
     * 划账接口
     *
     * @param trxNo 交易单号
     * @param notes 描述
     * @return Transaction
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction transfer(String trxNo, String notes) {
        Transaction transaction = transactionDao.get(trxNo);
        Project project = this.projectDao.get(transaction.getProject());
        if (transaction.getStatus() == TxStatus.close) {
            throw new ValidationException("交易已经关闭,不能划账");
        }
        if (transaction.getStatus() == TxStatus.success) {
            throw new ValidationException("交易已经完成,不能划账");
        }
        //更新交易状态
        if (ProjectType.withdraw == project.getType() || ProjectType.deposit == project.getType()) {
            if (transaction.getChannel() == TxChannel.offline) {
                transaction.setStatus(TxStatus.unprocessed);
            } else if (transaction.getChannel() == TxChannel.internal || transaction.getChannel() == TxChannel.card) {
                transaction.setStatus(TxStatus.success);
            }
            transaction.setStatusText(transaction.getStatus().getValue());
            transaction.setNotes(notes);
        } else {
            transaction.setStatus(TxStatus.success);
            transaction.setStatusText(transaction.getStatus().getValue());
            transaction.setNotes(notes);
        }

        // 防止 PayConfigName 为 null 的问题
        if (StringUtil.isBlank(transaction.getPayConfigName())) {
            transaction.setPayConfigName(transaction.getChannel().getValue());
        }

        // 转出
        this.out(transaction.getFrom(), transaction.getAmount(), project, transaction);
        // 转入
        this.in(transaction.getTo(), transaction.getAmount(), project, transaction);
        // 更新交易状态
        return transactionDao.save(transaction);
    }

    private void in(String account, BigDecimal amount, Project project, Transaction transaction) {
        if (project.getType().getAccountNotNull() == ProjectType.AccountNotNull.FROM) {
            return;
        }
        if (StringUtil.isBlank(transaction.getTo())) {
            throw new ValidationException("收款方为空");
        }
        Account to = this.accountDao.get(account);
        to.setAmount(to.getAmount().add(amount));
        // 充值积分处理
        if (Project.INPOUR.equals(project.getKey()) && Card.SUBJECT_BY_CARD_INPOUR.equals(transaction.getSubject())) {
            Card card = this.cardDao.get(transaction.get(Transaction.CARD_ID));
            ExtraService service = ObjectUtil.find(card.getExtras(), "project", ExtraService.ExtraProject.point);
            if (service != null) {
                to.setPoints(to.getPoints() + service.getValue());
                this.addPonit(to, (long) service.getValue(), "会员卡充值奖励");
            }
        }
        this.addBill(BillType.credit, transaction, to);//添加对应账单
        this.accountDao.update(to);
    }

    private void out(String account, BigDecimal amount, Project project, Transaction transaction) {
        if (project.getType().getAccountNotNull() == ProjectType.AccountNotNull.TO) {
            return;
        }
        if (StringUtil.isBlank(account)) {
            throw new ValidationException("付款方为空");
        }
        Account from = this.accountDao.get(account);
        if (from.getAmount().compareTo(amount) < 0) {
            throw new ValidationException("账户余额不足,交易失败");
        }
        from.setAmount(from.getAmount().subtract(amount));
        this.addBill(BillType.debit, transaction, from);//添加对应账单
        this.accountDao.update(from);
    }

    private void addPonit(Account account, Long number, String notes) {
        Point point = new Point();
        point.setAccount(account);
        point.setExpire(null);
        point.setType(PointType.plus);
        point.setPoint(number);
        point.setStatus(PointStatus.finished);
        point.setNotes(notes);
        this.pointDao.save(point);
    }

    private void addBill(BillType type, Transaction transaction, Account account) {
        Project project = this.projectDao.get(transaction.getProject());
        Bill bill = new Bill();
        bill.setAccount(account);
        bill.setType(type);
        bill.setAmount(transaction.getAmount());
        bill.setProject(project.getName());
        bill.setPaymentMethod(transaction.getPayConfigName());
        bill.setBalance(account.getAmount());
        this.billDao.save(bill);
    }

    @Transactional
    public Account activate(String no, String password) {
        Account account = this.accountDao.get(no);
        account.setPassword(passwordEncoder.encode(password));
        return this.accountDao.save(account);
    }

    @Autowired
    public void setApiGatewaySettings(ApiGatewaySettings apiGatewaySettings) {
        this.apiGatewaySettings = apiGatewaySettings;
    }

}
