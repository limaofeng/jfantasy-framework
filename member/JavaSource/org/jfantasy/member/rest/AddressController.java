package org.jfantasy.member.rest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.member.bean.Address;
import org.jfantasy.member.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 地址库 **/
@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /** 地址列表 **/
    @RequestMapping(method = RequestMethod.GET)
    @ApiImplicitParam(value = "filters",name = "filters",paramType = "query",dataType = "string")
    public Pager<Address> search(Pager<Address> pager,@ApiParam(hidden = true) List<PropertyFilter> filters) {
        return this.addressService.findPager(pager, filters);
    }

    /** 添加地址 **/
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Address create(@RequestBody Address address) {
        return this.addressService.save(address);
    }

    /** 查看地址 **/
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Address view(@PathVariable("id") Long id) {
        return get(id);
    }

    /** 更新地址 **/
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Address update(@PathVariable("id") Long id, @RequestBody Address address) {
        address.setId(id);
        return this.addressService.update(address);
    }

    /** 删除地址 **/
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.addressService.deltele(id);
    }

    private Address get(Long id) {
        Address address = this.addressService.get(id);
        if (address == null) {
            throw new NotFoundException("[id =" + id + "]对应的收货信息不存在");
        }
        return address;
    }

}
