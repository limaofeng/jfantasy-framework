package org.jfantasy.security.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.security.bean.OrgHelpBean;
import org.jfantasy.security.bean.OrgRelation;
import org.jfantasy.security.bean.Organization;
import org.jfantasy.security.dao.OrgRelationDao;
import org.jfantasy.security.dao.OrganizationDao;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrganizationService {

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private OrgRelationDao orgRelationDao;


    public Pager<Organization> findPager(Pager<Organization> pager, List<PropertyFilter> filters) {
        return this.organizationDao.findPager(pager, filters);
    }


    public List<Organization> find(List<PropertyFilter> filters) {
        //顶级组织机构
        List<OrgRelation> orgRelations = this.orgRelationDao.find(filters);

        //顶级组织机构
        List<Organization> organizations = new ArrayList<Organization>();
        for (OrgRelation relation : orgRelations) {
            //当前组织机构
            Organization organization = relation.getOrganization();
            organizations.add(organization);
            getRelation(relation);
        }
        //获取树桩组织机构
        return organizations;
    }

    private void getRelation(OrgRelation relation) {
        //当前组织机构
        Organization organization = relation.getOrganization();
        //下级组织机构
        List<OrgRelation> childrenRelation = this.orgRelationDao.find(Restrictions.eq("orgDimension.id", relation.getOrgDimension().getId()), Restrictions.eq("parent.id", relation.getId()));
        List<Organization> childrenAtion = new ArrayList<Organization>();
        for (OrgRelation childRelation : childrenRelation) {
            childrenAtion.add(childRelation.getOrganization());
            getRelation(childRelation);
        }
        organization.setChildren(childrenAtion);
    }


    public Organization save(Organization organization) {
        //维护关系
        List<OrgHelpBean> orgHelpBeans = organization.getOrgHelpBeans();
        //先保存当前组织机构
        organization = this.organizationDao.save(organization);
        for (OrgHelpBean orgHelpBean : ObjectUtil.defaultValue(orgHelpBeans, new ArrayList<OrgHelpBean>())) {
            //当前层级关系
            OrgRelation newRelation = new OrgRelation();
            //上级组织
            Organization parent = orgHelpBean.getOrganization();
            if (parent != null) {//有上级组织
                //上级关系
                OrgRelation parentRelation = this.findUniqueRelation(Restrictions.eq("orgDimension.id", orgHelpBean.getOrgDimension().getId()), Restrictions.eq("organization.id", parent.getId()));
                //查询该组织机构有没有和维度之间的关系
                newRelation = this.orgRelationDao.findUnique(Restrictions.eq("organization.id",organization.getId()),Restrictions.eq("orgDimension.id",orgHelpBean.getOrgDimension().getId()));
                if(newRelation==null){
                    newRelation = new OrgRelation();
                    //层级
                    newRelation.setLayer(parentRelation.getLayer() + 1);
                    //排序
                    newRelation.setSort(parentRelation.getLayer() + 1);
                    newRelation.setPath(parentRelation.getPath() + "," + organization.getId());
                    newRelation.setParent(parentRelation);
                    //组织维度
                    newRelation.setOrgDimension(orgHelpBean.getOrgDimension());
                    //当前组织机构
                    newRelation.setOrganization(organization);
                }
            } else {//没有上级组织
                //查询该组织机构有没有和维度之间的关系
                newRelation = this.orgRelationDao.findUnique(Restrictions.eq("organization.id",organization.getId()),Restrictions.eq("orgDimension.id",orgHelpBean.getOrgDimension().getId()));
                if(newRelation==null){
                    newRelation = new OrgRelation();
                    //组织维度
                    newRelation.setOrgDimension(orgHelpBean.getOrgDimension());
                    //层级
                    newRelation.setLayer(1);
                    //排序
                    newRelation.setSort(1);
                    newRelation.setPath(organization.getId());
                    //当前组织机构
                    newRelation.setOrganization(organization);
                }

            }
            this.orgRelationDao.save(newRelation);
        }
        return organization;
    }

    public OrgRelation findUniqueRelation(Criterion... criterions) {
        return this.orgRelationDao.findUnique(criterions);
    }


    public Organization findUnique(Criterion... criterions) {
        return this.organizationDao.findUnique(criterions);
    }

    public void delete(String... ids) {
        for (String id : ids) {
            List<OrgRelation> orgRelations = this.orgRelationDao.find(Restrictions.eq("organization.id", id));
            //组织机构关系
            for (OrgRelation orgRelation : orgRelations) {
                List<OrgRelation> children = orgRelation.getChildren();
                if (children == null || children.isEmpty()) {
                    this.orgRelationDao.delete(orgRelation);
                } else {
                    for (OrgRelation child : children) {
                        //改变上级归属
                        OrgRelation parent = orgRelation.getParent();
                        if (parent == null) {
                            child.setLayer(1);
                            child.setSort(1);
                            child.setParent(null);
                        } else {
                            child.setLayer(parent.getLayer() + 1);
                            child.setSort(parent.getSort() + 1);
                            child.setParent(parent);
                        }
                        this.orgRelationDao.save(child);
                    }
                    this.orgRelationDao.delete(orgRelation);
                }
            }
            Organization organization = this.findUnique(Restrictions.eq("id", id));
            this.organizationDao.delete(organization);
        }
    }


}
