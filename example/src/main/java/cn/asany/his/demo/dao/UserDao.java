package cn.asany.his.demo.dao;


import cn.asany.his.demo.bean.User;
import org.jfantasy.framework.dao.jpa.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author limaofeng
 */
@Repository
public interface UserDao extends JpaRepository<User, Long> {

}