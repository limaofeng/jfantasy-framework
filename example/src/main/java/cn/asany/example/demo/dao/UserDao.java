package cn.asany.example.demo.dao;

import cn.asany.example.demo.domain.User;
import net.asany.jfantasy.framework.dao.jpa.AnyJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author limaofeng
 */
@Repository
public interface UserDao extends AnyJpaRepository<User, Long> {}
