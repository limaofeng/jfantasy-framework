package org.jfantasy.demo.dao;

import org.jfantasy.demo.bean.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 13/11/2017 2:44 PM
 */
@Repository
public interface DataDao extends JpaRepository<Data,Long> {

}
