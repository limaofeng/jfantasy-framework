package org.jfantasy.common.rest;

import org.jfantasy.common.bean.FtpConfig;
import org.jfantasy.common.service.FtpConfigService;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** FTP配置信息 **/
@RestController
@RequestMapping("/ftp-configs")
public class FtpController {

    @Autowired
    private FtpConfigService ftpConfigService;

    /** 查询FTP配置 - 筛选FTP配置信息，返回数据集 **/
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<FtpConfig> search(List<PropertyFilter> filters) {
        return this.ftpConfigService.find(filters, "id", "asc");
    }

    /** 获取FTP配置 **/
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public FtpConfig view(@PathVariable("id") Long id) {
        return this.ftpConfigService.get(id);
    }

    /** 删除FTP配置 **/
    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.ftpConfigService.delete(id);
    }

    /** 批量删除FTP配置 **/
    @RequestMapping(method = {RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody Long... id) {
        this.ftpConfigService.delete(id);
    }

    /** 添加FTP配置 **/
    @RequestMapping(method = {RequestMethod.POST})
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public FtpConfig create(@RequestBody FtpConfig ftpConfig) {
        return ftpConfigService.save(ftpConfig);
    }

    /** 更新FTP配置 **/
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public FtpConfig update(@PathVariable("id") Long id, @RequestBody FtpConfig ftpConfig) {
        ftpConfig.setId(id);
        return ftpConfigService.save(ftpConfig);
    }

}
