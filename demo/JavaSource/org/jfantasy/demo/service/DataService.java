package org.jfantasy.demo.service;

import org.jfantasy.demo.bean.Data;
import org.jfantasy.demo.dao.DataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 13/11/2017 2:44 PM
 */
@Service
public class DataService {

    @Autowired
    private DataDao dataDao;

    @Transactional
    public List<Data> getAll(){
        return dataDao.findAll();
    }

}
