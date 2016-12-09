package org.jfantasy.trade.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.jfantasy.card.bean.Card;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.rest.models.AccountForm;
import org.jfantasy.pay.rest.models.ActivateForm;
import org.jfantasy.pay.rest.models.TransactionForm;
import org.jfantasy.pay.rest.models.assembler.AccountResourceAssembler;
import org.jfantasy.pay.rest.models.assembler.TransactionResourceAssembler;
import org.jfantasy.trade.bean.*;
import org.jfantasy.trade.bean.enums.ProjectType;
import org.jfantasy.trade.bean.enums.ReportTargetType;
import org.jfantasy.trade.bean.enums.TimeUnit;
import org.jfantasy.trade.service.AccountService;
import org.jfantasy.trade.service.ProjectService;
import org.jfantasy.trade.service.ReportService;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    private AccountService accountService;
    private TransactionService transactionService;
    private ReportService reportService;
    private ProjectService projectService;

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

        Project project = this.projectService.get(form.getProject());

        if(ProjectType.withdraw == project.getType() || ProjectType.transfer  == project.getType()){
            form.setFrom(account.getSn());
        }else if(ProjectType.deposit == project.getType()){
            form.setTo(account.getSn());
        }
        return this.transactionService.save(form.getProject(), form.getFrom(), form.getTo(), form.getChannel(), form.getAmount(), form.getNotes(), data);
    }

    @JsonResultFilter(ignore = {
            @IgnoreProperty(pojo = Card.class, name = {"account", "secret"})
    })
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/cards")
    public ModelAndView cards(@PathVariable("id") String sn, RedirectAttributes attrs, Pager<Card> pager, List<PropertyFilter> filters) {
        attrs.addAttribute("EQS_account", sn);
        attrs.addAttribute("page", pager.getCurrentPage());
        attrs.addAttribute("per_page", pager.getPageSize());
        if (!pager.isOrderBySetted()) {
            pager.sort(Card.FIELDS_BY_MODIFY_TIME, Pager.SORT_DESC);
        }
        if (pager.isOrderBySetted()) {
            attrs.addAttribute("sort", pager.getOrderBy());
            attrs.addAttribute("order", pager.getOrder());
        }
        for (PropertyFilter filter : filters) {
            attrs.addAttribute(filter.getFilterName(), filter.getPropertyValue());
        }
        return new ModelAndView("redirect:/cards");
    }

    /**
     * 积分记录
     **/
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/points")
    public ModelAndView points(@PathVariable("id") String sn,RedirectAttributes attrs, Pager<Point> pager, List<PropertyFilter> filters) {
        attrs.addAttribute("EQS_account.sn", sn);
        attrs.addAttribute("page", pager.getCurrentPage());
        attrs.addAttribute("per_page", pager.getPageSize());
        if (pager.isOrderBySetted()) {
            attrs.addAttribute("sort", pager.getOrderBy());
            attrs.addAttribute("order", pager.getOrder());
        }
        for (PropertyFilter filter : filters) {
            attrs.addAttribute(filter.getFilterName(), filter.getPropertyValue());
        }
        return new ModelAndView("redirect:/points");
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

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

}
