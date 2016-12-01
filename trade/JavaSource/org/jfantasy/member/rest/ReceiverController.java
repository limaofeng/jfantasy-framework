package org.jfantasy.member.rest;

import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.member.bean.Receiver;
import org.jfantasy.member.rest.models.assembler.ReceiverResourceAssembler;
import org.jfantasy.member.service.ReceiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址
 */
@RestController
@RequestMapping("/receivers")
public class ReceiverController {

    private ReceiverResourceAssembler assembler = new ReceiverResourceAssembler();

    private final ReceiverService receiverService;

    @Autowired
    public ReceiverController(ReceiverService receiverService) {
        this.receiverService = receiverService;
    }

    /**
     * 查询收货地址
     *
     * @param filters 筛选
     * @return List<Receiver>
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ResultResourceSupport> search(List<PropertyFilter> filters) {
        return assembler.toResources(this.receiverService.find(filters, "isDefault", "desc"));
    }

    /**
     * 查看收货地址
     *
     * @param id ID
     * @return List<Receiver>
     */
    @GetMapping("/{id}")
    public ResultResourceSupport update(@PathVariable("id") Long id) {
        Receiver receiver = this.receiverService.get(id);
        if (receiver == null) {
            throw new NotFoundException("[id =" + id + "]对应的收货信息不存在");
        }
        return assembler.toResource(receiver);
    }

    /**
     * 添加收货地址
     *
     * @param receiver Receiver
     * @return Receiver
     */
    @PostMapping
    public ResultResourceSupport create(@Validated(RESTful.POST.class) @RequestBody Receiver receiver) {
        return assembler.toResource(this.receiverService.save(receiver));
    }

    /**
     * 更新收货地址
     *
     * @param id       ID
     * @param receiver 修改对象
     * @return Receiver
     */
    @PutMapping("/{id}")
    public ResultResourceSupport update(@PathVariable("id") Long id, @RequestBody Receiver receiver) {
        get(id);
        receiver.setId(id);
        return assembler.toResource(this.receiverService.update(receiver));
    }

    /**
     * 删除收货地址
     *
     * @param id ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        get(id);
        this.receiverService.deltele(id);
    }

    private Receiver get(Long id) {
        Receiver receiver = this.receiverService.get(id);
        if (receiver == null) {
            throw new NotFoundException("[id =" + id + "]对应的收货信息不存在");
        }
        return receiver;
    }

}
