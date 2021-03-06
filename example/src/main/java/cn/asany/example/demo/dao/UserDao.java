package cn.asany.example.demo.dao;

import cn.asany.example.demo.domain.User;
import org.jfantasy.framework.dao.jpa.JpaRepository;
import org.springframework.stereotype.Repository;

/** @author limaofeng */
@Repository
public interface UserDao extends JpaRepository<User, Long> {}
