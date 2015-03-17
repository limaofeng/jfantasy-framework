package com.fantasy.security.web;

import com.fantasy.framework.struts2.ActionSupport;
import com.fantasy.security.bean.OrgDimension;
import com.fantasy.security.service.OrgDimensionService;
import org.hibernate.criterion.Restrictions;

import javax.annotation.Resource;

/**
 * Created by yhx on 2015/1/21.
 */
public class OrgDimensionAction extends ActionSupport {

    @Autowired
    private OrgDimensionService orgDimensionService;

    public String index() {
        this.attrs.put(ROOT, this.orgDimensionService.find());
        return JSONDATA;
    }

    public String save(OrgDimension orgDimension) {
        this.orgDimensionService.save(orgDimension);
        return JSONDATA;
    }

    public String view(String id) {
        this.attrs.put(ROOT, this.orgDimensionService.findUnique(Restrictions.eq("id", id)));
        return JSONDATA;
    }

    public String delete(String... ids) {
        this.orgDimensionService.delete(ids);
        return JSONDATA;
    }


}
