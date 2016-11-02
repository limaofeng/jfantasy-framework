package org.jfantasy.pay.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.pay.bean.*;
import org.jfantasy.pay.bean.enums.ReportTargetType;
import org.jfantasy.pay.bean.enums.TimeUnit;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.rest.models.AccountForm;
import org.jfantasy.pay.rest.models.ActivateForm;
import org.jfantasy.pay.rest.models.TransactionForm;
import org.jfantasy.pay.rest.models.assembler.AccountResourceAssembler;
import org.jfantasy.pay.rest.models.assembler.TransactionResourceAssembler;
import org.jfantasy.pay.service.AccountService;
import org.jfantasy.pay.service.ReportService;
import org.jfantasy.pay.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 账户
 **/
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private AccountResourceAssembler assembler = new AccountResourceAssembler();

    private ConcurrentMap<String, TransactionResourceAssembler> transactionAssemblers = new ConcurrentHashMap<>();

    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private PointController pointController;
    @Autowired
    private ReportService reportService;

    /**
     * 查询账户
     **/
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<ResultResourceSupport> search(Pager<Account> pager, List<PropertyFilter> filters) {
        return assembler.toResources(accountService.findPager(pager, filters));
    }

    /**
     * 账户详情
     **/
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResultResourceSupport view(@PathVariable("id") String id) {
        return assembler.toResource(get(id));
    }

    /**
     * 添加账户
     **/
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResultResourceSupport save(@RequestBody AccountForm form) {
        return assembler.toResource(this.accountService.save(form.getType(), form.getOwner(), form.getPassword()));
    }

    /**
     * 激活账户
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/activate")
    @ResponseBody
    public ResultResourceSupport activate(@PathVariable("id") String sn, @RequestBody ActivateForm form) {
        return assembler.toResource(this.accountService.activate(sn, form.getPassword()));
    }

    /**
     * 账户交易详情
     **/
    @JsonResultFilter(ignore = {
            @IgnoreProperty(pojo = Project.class, name = {"description", Transaction.BASE_JSONFIELDS})
    })
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/transactions")
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<ResultResourceSupport> transactions(@PathVariable("id") String sn, Pager<Transaction> pager, List<PropertyFilter> filters) {
        filters.add(new PropertyFilter("EQS_from_OR_to", sn));
        if (!pager.isOrderBySetted()) {
            pager.setOrderBy(Transaction.FIELDS_BY_CREATE_TIME);
            pager.setOrder(Pager.SORT_DESC);
        }
        if (!transactionAssemblers.containsKey(sn)) {
            transactionAssemblers.putIfAbsent(sn, new TransactionResourceAssembler(sn));
        }
        return transactionAssemblers.get(sn).toResources(transactionService.findPager(pager, filters));
    }

    @JsonResultFilter(ignore = {
            @IgnoreProperty(pojo = Project.class, name = "description")
    })
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/transactions")
    @ResponseBody
    public Transaction transactions(@PathVariable("id") String sn, @Validated(RESTful.POST.class) @RequestBody TransactionForm form) throws PayException {
        Account account = get(sn);
        Map<String, Object> data = new HashMap<>();
        data.put(Transaction.UNION_KEY, sn + "|" + (DateUtil.now().getTime() / 1000 * 10) + "|" + form.getAmount().setScale(2, BigDecimal.ROUND_UP).toString() + "|" + form.getChannel());
        return this.transactionService.save(form.getProject(), account.getSn(), form.getTo(), form.getChannel(), form.getAmount(), form.getNotes(), data);
    }

    /**
     * 积分记录
     **/
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/points")
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<ResultResourceSupport> points(@PathVariable("id") String sn, Pager<Point> pager, List<PropertyFilter> filters) {
        filters.add(new PropertyFilter("EQS_account.sn", sn));
        return pointController.search(pager, filters);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/reports")
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<Report> reports(@PathVariable("id") String sn, @RequestParam(value = "time_unit", required = false) TimeUnit timeUnit, Pager<Report> pager, List<PropertyFilter> filters) {
        filters.add(new PropertyFilter("EQE_targetType", ReportTargetType.account));
        filters.add(new PropertyFilter("EQS_targetId", sn));
        if (timeUnit != null) {
            filters.add(new PropertyFilter("EQE_timeUnit", timeUnit));
        }
        if (!pager.isOrderBySetted()) {
            pager.setOrderBy("time");
            pager.setOrder(Pager.SORT_DESC);
        }
        return reportService.findPager(pager, filters);
    }

    private Account get(String id) {
        return accountService.get(id);
    }

}
