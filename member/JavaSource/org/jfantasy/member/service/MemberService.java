package org.jfantasy.member.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.service.PasswordTokenEncoder;
import org.jfantasy.framework.service.PasswordTokenType;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.BeanUtil;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.regexp.RegexpConstant;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.member.Profile;
import org.jfantasy.member.ProfileFactory;
import org.jfantasy.member.ProfileService;
import org.jfantasy.member.bean.Member;
import org.jfantasy.member.bean.MemberDetails;
import org.jfantasy.member.bean.MemberTarget;
import org.jfantasy.member.bean.enums.SignUpType;
import org.jfantasy.member.dao.MemberDao;
import org.jfantasy.member.dao.MemberTargetDao;
import org.jfantasy.member.dao.MemberTypeDao;
import org.jfantasy.member.event.LoginEvent;
import org.jfantasy.member.event.LogoutEvent;
import org.jfantasy.member.event.RegisterEvent;
import org.jfantasy.sns.bean.Snser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 会员管理
 */
@Service
@Transactional
public class MemberService implements ProfileService {


    private static final Log LOG = LogFactory.getLog(MemberService.class);

    private final MemberDao memberDao;
    private final MemberTypeDao memberTypeDao;
    private final MemberTargetDao memberTargetDao;

    private ApplicationContext applicationContext;
    private PasswordEncoder passwordEncoder;
    private PasswordTokenEncoder passwordTokenEncoder;

    @Autowired
    public MemberService(MemberDao memberDao, MemberTypeDao memberTypeDao, MemberTargetDao memberTargetDao) {
        this.memberDao = memberDao;
        this.memberTypeDao = memberTypeDao;
        this.memberTargetDao = memberTargetDao;
    }

    /**
     * 列表查询
     *
     * @param pager   分页
     * @param filters 查询条件
     * @return Pager<Member>
     */
    public Pager<Member> findPager(Pager<Member> pager, List<PropertyFilter> filters) {
        return this.memberDao.findPager(pager, filters);
    }

    public Member signUp(String username, SignUpType signUpType) {
        return this.signUp(username, null, signUpType);
    }

    public Member signUp(String username, String password, SignUpType signUpType) {
        Member member = new Member();
        member.setUsername(username);
        member.setPassword(StringUtil.defaultValue(password, StringUtil.generateNonceString(20)));
        return this.save(member, signUpType);
    }

    public Member login(String userType, String username) {
        Member member = this.findUnique(username);
        if (member == null) {//用户不存在
            throw new ValidationException(100301, "用户名和密码错误");
        }
        if (!member.getEnabled()) {
            throw new ValidationException(100302, "用户被禁用");
        }
        if (!member.getAccountNonLocked()) {
            throw new ValidationException(100303, "用户被锁定");
        }
        member.setLastLoginTime(DateUtil.now());
        validateUserType(member, userType);
        this.memberDao.update(member);
        Member mirror = ObjectUtil.clone(member);
        this.applicationContext.publishEvent(new LoginEvent(mirror));
        LOG.debug(mirror);
        return mirror;
    }

    /**
     * 会员登录接口
     *
     * @param username 用户名
     * @param password 密码
     * @return Member
     */
    public Member login(PasswordTokenType type, String userType, String username, String password) {
        Member member = this.findUnique(type, username);

        if (!this.passwordTokenEncoder.matches("login", type, username, member != null ? member.getPassword() : "", password)) {
            throw new ValidationException(100101, "用户名和密码错误");
        }

        // 个人短信登陆，自动完成注册
        if (member == null && type == PasswordTokenType.macode && Member.MEMBER_TYPE_PERSONAL.equals(userType)) {
            member = signUp(username, SignUpType.sms);
        }

        if (type == PasswordTokenType.macode && (member == null || !ObjectUtil.exists(member.getTypes(), "id", userType))) {
            Profile profile = getProfile(userType, username);
            if (profile == null) {
                throw new ValidationException("您还没有入住平台");
            }
            if (member == null) {
                member = signUp(username, SignUpType.sms);
            }
            member.addTarget(connect(member.getId(), userType, profile.getId()));
        }

        if (member == null) {
            throw new ValidationException(100101, "用户不存在");
        }

        return login(userType, username);
    }

    private Profile getProfile(String type, String phone) {
        return getProfileService(type).loadProfileByPhone(phone);
    }

    private ProfileService getProfileService(String type) {
        ProfileFactory profileFactory = SpringContextUtil.getBeanByType(ProfileFactory.class);
        ProfileService profileService = profileFactory.getProfileService(type);
        if (profileService == null) {
            throw new ValidationException("type 对应的 ProfileService 缺失");
        }
        return profileService;
    }

    private static Member validateUserType(Member member, String userType) {
        if (StringUtil.isNotBlank(userType) && !ObjectUtil.exists(member.getTypes(), "id", userType)) {
            throw new RestException("UserType 不一致");
        }
        member.setType(userType);
        return member;
    }

    /**
     * 前台注册页面保存
     *
     * @param member 注册信息
     * @return Member
     */
    public Member save(Member member, SignUpType signUpType) {
        MemberDetails details = ObjectUtil.defaultValue(member.getDetails(), new MemberDetails());
        // 设置默认属性
        details.setLevel(0L);
        if (signUpType == SignUpType.email) {// 如果用email注册
            details.setEmail(member.getUsername());
        } else if (signUpType == SignUpType.sms) {// 如果用手机注册
            details.setMobile(member.getUsername());
        }
        member.setDetails(details);
        // 默认昵称与用户名一致
        if (StringUtil.isBlank(member.getNickName())) {
            member.setNickName(member.getUsername());
        }
        // 初始化用户状态
        member.setEnabled(true);
        member.setAccountNonLocked(true);
        member.setAccountNonExpired(true);
        member.setCredentialsNonExpired(true);
        // 保存用户
        this.memberDao.save(member);
        // 设置默认用户类型
        member.addTarget(connect(member.getId(), Member.MEMBER_TYPE_PERSONAL, details.getMemberId().toString()));
        // 公布事件
        applicationContext.publishEvent(new RegisterEvent(member));
        return member;
    }

    public MemberDetails update(MemberDetails details) {
        Member member = this.memberDao.get(details.getMemberId());
        BeanUtil.copyProperties(member.getDetails(), details, "memberId", "member", "level", "mobileValid", "mailValid");
        this.memberDao.update(member);
        return member.getDetails();
    }

    public List<Member> find(Criterion... criterions) {
        return this.memberDao.find(criterions);
    }

    public Member changePassword(Long id, PasswordTokenType type, String oldPassword, String newPassword) {
        Member member = this.memberDao.get(id);
        if (member == null) {
            throw new NotFoundException("用户不存在");
        }
        if (!member.getEnabled()) {
            throw new ValidationException(100301, "用户已被禁用");
        }

        if (!this.passwordTokenEncoder.matches("password", type, member.getUsername(), member.getPassword(), oldPassword)) {
            throw new ValidationException(100000, "提供的 password token 不正确!");
        }

        member.setPassword(passwordEncoder.encode(newPassword));
        return this.memberDao.update(member);
    }

    public void update(Snser snser) {
        Member member = this.memberDao.get(snser.getMember().getId());
        if (StringUtil.isBlank(member.getNickName()) || member.getUsername().equals(member.getNickName())) {
            member.setNickName(snser.getName());
        }
        MemberDetails details = member.getDetails();
        if (details.getAvatar() == null) {
            details.setAvatar(snser.getAvatar());
        }
        if (details.getSex() == null) {
            details.setSex(snser.getSex());
        }
    }

    /**
     * 保存对象
     *
     * @param member member
     * @return Member
     */
    public Member update(Member member, boolean patch) {
        return this.memberDao.save(member, patch);
    }

    /**
     * 获取对象
     *
     * @param id id
     * @return Member
     */
    public Member get(Long id) {
        return this.memberDao.get(id);
    }

    /**
     * 根据id 批量删除
     *
     * @param ids ids
     */
    public void delete(Long... ids) {
        for (Long id : ids) {
            this.memberDao.delete(id);
        }
    }

    @Override
    public Profile loadProfileByPhone(String name) {
        Member member = this.memberDao.findUnique(Restrictions.eq("details.mobile", name));
        if (member == null) {
            return null;
        }
        return member.getDetails();
    }

    /**
     * 退出
     *
     * @param username 用户名
     */
    public void logout(String username) {
        this.applicationContext.publishEvent(new LogoutEvent(findUniqueByUsername(username)));
    }

    /**
     * 连接服务
     *
     * @param id    ID
     * @param type  类型
     * @param value 目标
     */
    public MemberTarget connect(Long id, String type, String value) {
        return this.memberTargetDao.save(MemberTarget.newInstance(this.memberDao.get(id), this.memberTypeDao.get(type), value));
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return Member
     */
    public Member findUniqueByUsername(String username) {
        return this.memberDao.findUniqueBy("username", username);
    }

    public Member findUniqueByPhone(String phone) {
        return this.memberDao.findUniqueBy("details.mobile", phone);
    }

    private Member findUnique(PasswordTokenType type, String username) {
        switch (type) {
            case macode:
                return findUniqueByPhone(username);
            case token:
            case password:
            default:
                return findUnique(username);
        }
    }

    private Member findUnique(String username) {
        Member member = this.memberDao.findUniqueBy("username", username);// 用户名
        if (member == null && RegexpUtil.isMatch(username, RegexpConstant.VALIDATOR_EMAIL)) {//  email
            member = this.memberDao.findUniqueBy("details.email", username);
        }
        if (member == null && RegexpUtil.isMatch(username, RegexpConstant.VALIDATOR_MOBILE)) {// 手机
            member = this.memberDao.findUniqueBy("details.mobile", username);
        }
        return member;
    }

    @Autowired(required = false)
    public void setPasswordTokenEncoder(PasswordTokenEncoder passwordTokenEncoder) {
        this.passwordTokenEncoder = passwordTokenEncoder;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

}
