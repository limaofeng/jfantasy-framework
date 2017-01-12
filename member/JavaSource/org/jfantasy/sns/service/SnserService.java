package org.jfantasy.sns.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.member.dao.MemberDao;
import org.jfantasy.security.bean.enums.Sex;
import org.jfantasy.sns.bean.Platform;
import org.jfantasy.sns.bean.Snser;
import org.jfantasy.sns.bean.enums.PlatformType;
import org.jfantasy.sns.dao.SnserDao;
import org.jfantasy.weixin.bean.Fans;
import org.jfantasy.weixin.service.FansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnserService {

    private final SnserDao snserDao;
    private final PlatformService platformService;
    private final FansService fansService;
    private final MemberDao memberDao;

    @Autowired
    public SnserService(MemberDao memberDao, SnserDao snserDao, PlatformService platformService, FansService fansService) {
        this.memberDao = memberDao;
        this.snserDao = snserDao;
        this.platformService = platformService;
        this.fansService = fansService;
    }

    @Transactional(readOnly = true)
    public List<Snser> snsers(Long id) {
        return this.snserDao.find(Restrictions.eq("member.id", id));
    }

    @Transactional(readOnly = true)
    public Snser get(Long id) {
        return this.snserDao.get(id);
    }

    public Snser get(PlatformType type, String appId, String openId) {
        return this.snserDao.findUnique(Restrictions.eq("type", type), Restrictions.eq("appId", appId), Restrictions.eq("openId", openId));
    }

    @Transactional
    public Snser save(Long memberId, PlatformType type, String appId, String openId) {
        Snser snser = this.snserDao.findUnique(Restrictions.eq("member.id", memberId), Restrictions.eq("type", type), Restrictions.eq("openId", openId));
        if (snser != null) {
            throw new ValidationException(String.format("用户已绑定其他 %s 账号，请先解绑。", type.toString()));
        }
        snser = get(type, appId, openId);
        if (snser != null) {
            throw new ValidationException(String.format("%s 已被其他账号绑定，请先解绑。", type.toString()));
        }
        Platform platform = this.platformService.findUnique(type, appId);
        if (platform.getType() == PlatformType.WeChat) {
            Fans fans = fansService.get(appId, openId);
            snser = new Snser();
            snser.setName(fans.getNickname());
            snser.setAvatar(null);
            snser.setSex(Sex.valueOf(fans.getSex().name()));
            snser.setPlatform(platform);
            snser.setMember(this.memberDao.get(memberId));
            snser.setOpenId(fans.getOpenId());
        }
        throw new ValidationException("暂不支持该的平台");
    }

    @Transactional
    public Snser save(Snser snser) {
        return this.snserDao.save(snser);
    }

    @Transactional
    public Snser update(Snser snser, boolean patch) {
        return this.snserDao.update(snser, patch);
    }

    @Transactional
    public void deltele(Long id) {
        this.snserDao.delete(id);
    }

}
