package com.fantasy.remind.web;

import com.fantasy.framework.dao.Pager;
import com.fantasy.framework.dao.hibernate.PropertyFilter;
import com.fantasy.framework.struts2.ActionSupport;
import com.fantasy.framework.util.jackson.JSON;
import com.fantasy.remind.bean.Model;
import com.fantasy.remind.bean.Notice;
import com.fantasy.remind.service.ModelService;
import com.fantasy.remind.service.NoticeService;
import net.sf.ehcache.search.expression.Not;

import javax.annotation.Resource;
import java.util.*;

/**
 * 提醒 action
 */

public class NoticeAction extends ActionSupport {

    @Resource
    private NoticeService noticeService;
    @Resource
    private ModelService modelService;

    public String index() throws Exception {
        this.search(new Pager<Notice>(),new ArrayList<PropertyFilter>());
        this.attrs.put("pager", this.attrs.get(ROOT));
        this.attrs.remove(ROOT);
        this.searchModel(new Pager<Model>(),new ArrayList<PropertyFilter>());
        this.attrs.put("models",this.attrs.get(ROOT));
        this.attrs.remove(ROOT);
        Notice notice2=new Notice();
        notice2.setModel(new Model("shuju"));
        Map<String,String> map=new HashMap<String, String>();
        map.put("key","123"+new Date().toString());
        map.put("content","/web");
        map.put("id","11");
        notice2.setReplaceMap(JSON.serialize(map));
        noticeService.save(notice2);
        return SUCCESS;
    }

    public String search(Pager<Notice> pager,List<PropertyFilter> filters){
        if(pager.getOrderBy()==null){
            pager.setOrderBy("modifyTime");
            pager.setOrder(Pager.Order.desc);
        }
        this.attrs.put(ROOT, this.noticeService.findPager(pager, filters));
        return JSONDATA;
    }
    public String searchModel(Pager<Model> pager,List<PropertyFilter> filters){
        if(pager.getOrderBy()==null){
            pager.setOrderBy("modifyTime");
            pager.setOrder(Pager.Order.desc);
        }
        this.attrs.put(ROOT,modelService.findPager(pager,filters));
        return JSONDATA;
    }

    public String save(Notice notice) throws Exception {
        this.noticeService.save(notice);
        return JSONDATA;
    }
    public String edit(Long id){
        this.attrs.put("notice", this.noticeService.get(id));
        return SUCCESS;
    }

    public String view(Long id){
        this.attrs.put("notice",this.noticeService.get(id));
        return SUCCESS;
    }

    public String delete(Long[] ids){
        this.noticeService.delete(ids);
        return JSONDATA;
    }

    public String saveModel(Model model) throws Exception {
        this.modelService.save(model);
        return JSONDATA;
    }
    public String editModel(String id){
        this.attrs.put("model",this.modelService.get(id));
        return SUCCESS;
    }

    public String viewMolde(String id){
        this.attrs.put("model",this.modelService.get(id));
        return SUCCESS;
    }
    public String deleteModel(String[] ids){
        this.modelService.delete(ids);
        return JSONDATA;
    }

    public String go(Long id) throws Exception {
        Notice n=this.noticeService.get(id);
        n.setIsRead(true);
        this.noticeService.save(n);
        this.attrs.put("url",n.getUrl());
        return SUCCESS;
    }



}
