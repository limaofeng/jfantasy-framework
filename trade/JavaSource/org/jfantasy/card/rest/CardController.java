package org.jfantasy.card.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.security.SpringSecurityUtils;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.oauth.userdetails.OAuthUserDetails;
import org.jfantasy.card.bean.Card;
import org.jfantasy.card.bean.CardBatch;
import org.jfantasy.card.bean.CardDesign;
import org.jfantasy.card.bean.CardType;
import org.jfantasy.pay.rest.models.CardBindForm;
import org.jfantasy.pay.rest.models.assembler.CardResourceAssembler;
import org.jfantasy.card.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员卡
 **/
@RestController
@RequestMapping("/cards")
public class CardController {

    public static CardResourceAssembler assembler = new CardResourceAssembler();

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * 查询卡列表
     *
     * @param pager
     * @param filters
     * @return
     */
    @JsonResultFilter(
            allow = {
                    @AllowProperty(pojo = CardType.class, name = {"key", "name"}),
                    @AllowProperty(pojo = CardDesign.class, name = {"key", "name"}),
                    @AllowProperty(pojo = CardBatch.class, name = {"no"})
            }
    )
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<ResultResourceSupport> search(Pager<Card> pager, List<PropertyFilter> filters) {
        return assembler.toResources(cardService.findPager(pager, filters));
    }

    /**
     * 卡片详情
     *
     * @param id 卡号
     * @return Card
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResultResourceSupport view(@PathVariable("id") String id) {
        return assembler.toResource(get(id));
    }

    @JsonResultFilter(
            allow = {
                    @AllowProperty(pojo = Card.class, name = {"no", "type", "design", "batch", "status", "amount", "extras"}),
                    @AllowProperty(pojo = CardType.class, name = {"key", "name"}),
                    @AllowProperty(pojo = CardDesign.class, name = {"key", "name"}),
                    @AllowProperty(pojo = CardBatch.class, name = {"no"})
            }
    )
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/bind")
    @ResponseBody
    public ResultResourceSupport bind(@PathVariable("id") String id, @RequestBody CardBindForm form) {
        String owner = StringUtil.isNotBlank(form.getMemberId()) ? form.getMemberId().toString() : null;
        if (form.getMemberId() == null) {
            OAuthUserDetails user = SpringSecurityUtils.getCurrentUser(OAuthUserDetails.class);
            if (user == null) {
                throw new RestException(401, "没有权限访问接口");
            }
            owner = user.getId().toString();
        }
        return assembler.toResource(this.cardService.bind(owner, id, form.getPassword()));
    }

    private Card get(String id) {
        return cardService.get(id);
    }

}
