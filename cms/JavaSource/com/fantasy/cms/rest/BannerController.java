package com.fantasy.cms.rest;

import com.fantasy.cms.bean.Banner;
import com.fantasy.cms.service.BannerService;
import com.fantasy.framework.dao.Pager;
import com.fantasy.framework.dao.hibernate.PropertyFilter;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @apiDefine paramBanner
 * @apiParam {String} key  编码
 * @apiParam {String} name  名称
 * @apiParam {String} size  图片尺寸
 * @apiParam {String} description  描述
 * @apiParam {List}  bannerItems  图片明细
 * @apiParam {String}  BannerItem.title  标题
 * @apiParam {String}  BannerItem.summary  摘要
 * @apiParam {String}  BannerItem.url  跳转地址
 * @apiParam {String}  BannerItem.sort  排序字段
 * @apiParam {String}  BannerItem.bannerImage 图片对象
 * @apiVersion 3.3.8
 */

/**
 * @apiDefine returnBanner
 * @apiSuccess {String} key  编码
 * @apiSuccess {String} name  名称
 * @apiSuccess {String} size  图片尺寸
 * @apiSuccess {String} description  描述
 * @apiSuccess {List}  bannerItems  图片明细
 * @apiSuccess {String}  BannerItem.id  明细ID
 * @apiSuccess {String}  BannerItem.title  标题
 * @apiSuccess {String}  BannerItem.summary  摘要
 * @apiSuccess {String}  BannerItem.url  跳转地址
 * @apiSuccess {String}  BannerItem.sort  排序字段
 * @apiSuccess {String}  BannerItem.bannerImage 图片对象
 * @apiSuccess {Banner}  BannerItem.banner Banner对象
 * @apiVersion 3.3.8
 */
@Api(value = "cms-banners", description = "轮播图接口")
@RestController
@RequestMapping("/cms/banners")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * @api {post} /cms/banners  添加轮播图
     * @apiVersion 3.3.8
     * @apiName createBanner
     * @apiGroup 内容管理
     * @apiDescription 添加轮播图
     * @apiPermission admin
     * @apiExample Example usage:
     * curl -i -X POST http://localhost/cms/banners
     * @apiUse paramBanner
     * @apiUse returnBanner
     * @apiUse GeneralError
     */
    @ApiOperation(value = "添加轮播图", notes = "添加轮播图", response = Banner.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Banner create(@RequestBody Banner banner) {
        return bannerService.save(banner);
    }

    /**
     * @api {put} /cms/banners/:key  更新轮播图
     * @apiVersion 3.3.8
     * @apiName updateBanner
     * @apiGroup 内容管理
     * @apiDescription 更新轮播图信息
     * @apiPermission admin
     * @apiExample Example usage:
     * curl -i -X PUT http://localhost/cms/banners/root
     * @apiUse paramBanner
     * @apiUse returnBanner
     * @apiUse GeneralError
     */
    @ApiOperation(value = "更新轮播图", notes = "更新轮播图信息", response = Banner.class)
    @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
    @ResponseBody
    public Banner update(@PathVariable("key") String key,@RequestBody Banner banner) {
        banner.setKey(key);
        return bannerService.save(banner);
    }

    /**
     * @api {get} /cms/banners/:key  获取轮播图
     * @apiVersion 3.3.8
     * @apiName getBanner
     * @apiGroup 内容管理
     * @apiDescription 获取轮播图
     * @apiPermission admin
     * @apiExample Example usage:
     * curl -i http://localhost/cms/banners/root
     * @apiUse returnBanner
     * @apiUse GeneralError
     */
    @ApiOperation(value = "获取轮播图", notes = "获取轮播图", response = Banner.class)
    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @ResponseBody
    public Banner view(@PathVariable("key") String key) {
        return bannerService.get(key);
    }

    /**
     * @api {delete} /cms/banners/:key 删除轮播图
     * @apiVersion 3.3.8
     * @apiName deleteBanner
     * @apiGroup 内容管理
     * @apiDescription 删除轮播图
     * @apiPermission admin
     * @apiExample Example usage:
     * curl -i -X DELETE http://localhost/cms/banners/root
     * @apiUse GeneralError
     */
    @ApiOperation(value = "删除轮播图", notes = "删除轮播图")
    @RequestMapping(value = "/{key}", method = {RequestMethod.DELETE})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("key") String key) {
        bannerService.delete(key);
    }

    /**
     * @param key bannerId-1
     * @param key bannerId-2
     * @api {batchDelete} /cms/banners/:key 批量删除轮播图
     * @apiVersion 3.3.8
     * @apiName deleteBanner
     * @apiGroup 内容管理
     * @apiDescription 批量删除轮播图
     * @apiPermission admin
     * @apiExample Example usage:
     * curl -i -X DELETE http://localhost/cms/banners/root
     * @apiUse GeneralError
     */
    @ApiOperation(value = "批量删除轮播图", notes = "批量删除轮播图")
    @RequestMapping(method = {RequestMethod.DELETE})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@RequestBody String... key) {
        bannerService.delete(key);
    }

    /**
     * @api {get} /cms/banners   查询轮播图
     * @apiVersion 3.3.8
     * @apiName getBanners
     * @apiGroup 内容管理
     * @apiDescription 查询轮播图
     * @apiPermission admin
     * @apiExample Example usage:
     * curl -i http://localhost/cms/banners?currentPage=1&EQS_key=test
     * @apiUse paramPager
     * @apiUse paramPropertyFilter
     * @apiUse returnPager
     * @apiUse GeneralError
     */
    @ApiOperation(value = "查询轮播图", notes = "查询轮播图", response = Pager.class)
    @RequestMapping(method = RequestMethod.GET)
    public Pager<Banner> search(@ApiParam(value = "分页对象", name = "pager") Pager<Banner> pager, @ApiParam(value = "过滤条件", name = "filters") List<PropertyFilter> filters) {
        return this.bannerService.findPager(pager, filters);
    }
}

