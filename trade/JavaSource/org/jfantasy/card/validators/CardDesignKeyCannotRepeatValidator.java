package org.jfantasy.card.validators;

import org.jfantasy.framework.spring.validation.ValidationException;
import org.jfantasy.framework.spring.validation.Validator;
import org.jfantasy.card.service.CardDesignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CardDesignKeyCannotRepeatValidator  implements Validator<String> {

    private final CardDesignService cardDesignService;

    @Autowired
    public CardDesignKeyCannotRepeatValidator(CardDesignService cardDesignService) {
        this.cardDesignService = cardDesignService;
    }

    @Override
    public void validate(String value) throws ValidationException {
        if (cardDesignService.get(value) != null) {
            throw new ValidationException("KEY["+value+"]已经存在");
        }
    }

}
