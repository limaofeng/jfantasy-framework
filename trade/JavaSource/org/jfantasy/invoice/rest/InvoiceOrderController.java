package org.jfantasy.invoice.rest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.invoice.bean.InvoiceOrder;
import org.jfantasy.invoice.rest.models.assembler.InvoiceOrderResourceAssembler;
import org.jfantasy.invoice.service.InvoiceOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * 开票订单
 **/
@RestController
@RequestMapping(value = "/iorders", produces = {APPLICATION_JSON_VALUE})
public class InvoiceOrderController {

    protected static InvoiceOrderResourceAssembler assembler = new InvoiceOrderResourceAssembler();

    private final InvoiceOrderService invoiceOrderService;

    @Autowired
    public InvoiceOrderController(InvoiceOrderService invoiceOrderService) {
        this.invoiceOrderService = invoiceOrderService;
    }

    /**
     * 发票订单列表
     * @param pager
     * @param filters
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiImplicitParam(value = "filters",name = "filters",paramType = "query",dataType = "string")
    public Pager<ResultResourceSupport> search(Pager<InvoiceOrder> pager,@ApiParam(hidden = true) List<PropertyFilter> filters) {
        if(!pager.isOrderBySetted()){
            pager.setOrderBy(InvoiceOrder.FIELDS_BY_CREATE_TIME);
            pager.setOrder(Pager.SORT_DESC);
        }
        return assembler.toResources(this.invoiceOrderService.findPager(pager, filters));
    }

    /**
     * 添加发票接口
     * @param order
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResultResourceSupport save(@RequestBody InvoiceOrder order) {
        return assembler.toResource(this.invoiceOrderService.save(order));
    }

}