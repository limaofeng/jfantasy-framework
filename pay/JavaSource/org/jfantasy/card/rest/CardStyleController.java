package org.jfantasy.card.rest;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.card.bean.CardDesign;
import org.jfantasy.card.bean.Styles;
import org.jfantasy.card.bean.enums.CardDesignStatus;
import org.jfantasy.pay.rest.models.CardStyle;
import org.jfantasy.card.service.CardDesignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/** 会员风格 **/
@RestController
@RequestMapping("/card-styles")
public class CardStyleController {

    private final CardDesignService cardDesignService;

    @Autowired
    public CardStyleController(CardDesignService cardDesignService) {
        this.cardDesignService = cardDesignService;
    }

    /** 会员风格 **/
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<CardStyle> search() {
        List<CardStyle> cardStyles = new ArrayList<>();
        for (CardDesign design : cardDesignService.find(Restrictions.eq("status", CardDesignStatus.publish))) {
            Styles styles = new Styles();
            styles.setBg_front("http://static-jfantasy-org.oss-cn-hangzhou.aliyuncs.com/image/lALOYERoIc0BZ80CMA_560_359.png");
            styles.setBg_back("http://static-jfantasy-org.oss-cn-hangzhou.aliyuncs.com/image/lALOYERoIs0BZ80CMA_560_359.png");
            //TODO 后期需要删除的逻辑
            cardStyles.add(new CardStyle(design.getKey(), styles));
        }
        return cardStyles;
    }

}
