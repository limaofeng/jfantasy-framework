package org.jfantasy.member.rest.validators;

import org.jfantasy.framework.spring.validation.ValidationException;
import org.jfantasy.framework.spring.validation.Validator;
import org.jfantasy.member.service.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MobileByTeamMemberCannotRepeatValidator implements Validator<String> {

    private final TeamMemberService teamMemberService;

    @Autowired
    public MobileByTeamMemberCannotRepeatValidator(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }


    @Override
    public void validate(String value) throws ValidationException {

    }
}
