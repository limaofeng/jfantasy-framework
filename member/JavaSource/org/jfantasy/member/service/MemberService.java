package org.jfantasy.member.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.service.PasswordTokenEncoder;
import org.jfantasy.framework.service.PasswordTokenType;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.BeanUtil;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.regexp.RegexpCst;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.member.bean.Member;
import org.jfantasy.member.bean.MemberDetails;
import org.jfantasy.member.dao.MemberDao;
import org.jfantasy.member.event.LoginEvent;
import org.jfantasy.member.event.LogoutEvent;
import org.jfantasy.member.event.RegisterEvent;
import org.jfantasy.security.bean.Role;
import org.jfantasy.security.service.RoleService;
import org.jfantasy.sns.bean.Snser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员管理
 */
@Service
@Transactional
public class MemberService {

    private static final String DEFAULT_ROLE_CODE = "MEMBER";

    private static final Log LOG = LogFactory.getLog(MemberService.class);

    private MemberDao memberDao;
    private RoleService roleService;
    private ApplicationContext applicationContext;
    private PasswordEncoder passwordEncoder;
    private PasswordTokenEncoder passwordTokenEncoder;

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

    private Member signUp(String username) {
        Member member = new Member();
        member.setType(Member.MEMBER_TYPE_PERSONAL);
        member.setUsername(username);
        member.setPassword(StringUtil.generateNonceString(20));
        return this.save(member);
    }

    public Member login(String username) {
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
    public Member login(PasswordTokenType type, String username, String password) {
        Member member = this.findUnique(username);

        if (!this.passwordTokenEncoder.matches("login", type, username, member != null ? member.getPassword() : "", password)) {
            throw new ValidationException(100101, "用户名和密码错误");
        }

        if (member == null && type == PasswordTokenType.macode) {
            signUp(username);
        }
        return login(username);
    }

    /**
     * 前台注册页面保存
     *
     * @param member 注册信息
     * @return Member
     */
    public Member save(Member member) {
        if (Member.MEMBER_TYPE_PERSONAL.equals(member.getType())) {
            MemberDetails details = ObjectUtil.defaultValue(member.getDetails(), new MemberDetails());
            // 设置默认属性
            details.setMailValid(false);
            details.setMobileValid(false);
            details.setLevel(0L);
            // 如果用email注册
            if (RegexpUtil.isMatch(member.getUsername(), RegexpCst.VALIDATOR_EMAIL)) {
                details.setEmail(member.getUsername());
            }
            // 如果用手机注册
            if (RegexpUtil.isMatch(member.getUsername(), RegexpCst.VALIDATOR_MOBILE)) {
                details.setMobile(member.getUsername());
            }
            member.setDetails(details);
        }

        // 默认昵称与用户名一致
        if (StringUtil.isBlank(member.getNickName())) {
            member.setNickName(member.getUsername());
        }

        // 初始化用户权限
        List<Role> roles = new ArrayList<>();
        Role defaultRole = roleService.get(DEFAULT_ROLE_CODE);
        if (defaultRole != null) {
            roles.add(defaultRole);
        }
        member.setRoles(roles);
        member.setUserGroups(new ArrayList<>());
        //初始化用户状态
        member.setEnabled(true);
        member.setAccountNonLocked(true);
        member.setAccountNonExpired(true);
        member.setCredentialsNonExpired(true);
        // 保存用户
        this.memberDao.save(member);
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

    /**
     * 退出
     *
     * @param username 用户名
     */
    public void logout(String username) {
        this.applicationContext.publishEvent(new LogoutEvent(findUniqueByUsername(username)));
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

    private Member findUnique(String username) {
        if (RegexpUtil.isMatch(username, RegexpCst.VALIDATOR_EMAIL)) {//  email
            return this.memberDao.findUniqueBy("details.email", username);
        }
        if (RegexpUtil.isMatch(username, RegexpCst.VALIDATOR_MOBILE)) {// 手机
            return this.memberDao.findUniqueBy("details.mobile", username);
        }
        return this.memberDao.findUniqueBy("username", username);// 用户名
    }

    @Autowired(required = false)
    public void setPasswordTokenEncoder(PasswordTokenEncoder passwordTokenEncoder) {
        this.passwordTokenEncoder = passwordTokenEncoder;
    }

    @Autowired
    public void setMemberDao(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
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
