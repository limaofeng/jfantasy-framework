package com.fantasy.cms.web;

import com.fantasy.cms.bean.ArticleCategory;
import com.fantasy.cms.service.CmsService;
import com.fantasy.framework.dao.Pager;
import com.fantasy.framework.dao.hibernate.PropertyFilter;
import com.fantasy.framework.struts2.ActionSupport;
import com.fantasy.framework.util.jackson.JSON;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class ArticleCategoryAction extends ActionSupport {

    @Autowired
    private CmsService cmsService;

    public String create(ArticleCategory category){
        System.out.println(category);
        return SUCCESS;
    }

    public String search(Pager<ArticleCategory> pager,List<PropertyFilter> filters){
        System.out.println(JSON.text().serialize(pager));
        this.attrs.put(ROOT,this.cmsService.getCategorys());
        return SUCCESS;
    }

    public String view(String id) {
        this.attrs.put(ROOT, cmsService.get(id));
        return SUCCESS;
    }

    public String delete(String... id){
        System.out.println(Arrays.toString(id));
        return SUCCESS;
    }

    public String update(ArticleCategory category) {
        System.out.println(category);
        return SUCCESS;
    }

}
