package org.jfantasy.member.validators;

import org.jfantasy.framework.spring.validation.ValidationException;
import org.jfantasy.framework.spring.validation.Validator;
import org.jfantasy.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("member.UsernameCannotRepeatValidator")
public class UsernameCannotRepeatValidator implements Validator<String> {

    private final MemberService memberService;

    @Autowired
    public UsernameCannotRepeatValidator(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public void validate(String value) throws ValidationException {
        if (memberService.findUniqueByUsername(value) != null) {
            throw new ValidationException("用户名["+value+"]已经存在");
        }
    }

}
