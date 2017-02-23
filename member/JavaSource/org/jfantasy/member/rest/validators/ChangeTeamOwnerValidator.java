package org.jfantasy.member.rest.validators;

import org.jfantasy.framework.spring.validation.ValidationException;
import org.jfantasy.framework.spring.validation.Validator;
import org.jfantasy.member.service.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChangeTeamOwnerValidator implements Validator<Long> {

    private final TeamMemberService teamMemberService;

    @Autowired
    public ChangeTeamOwnerValidator(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    @Override
    public void validate(Long value) throws ValidationException {
        if (value != null && teamMemberService.get(value) == null) {
            throw new ValidationException("ID[" + value + "]对于的TeamMember不存在");
        }
    }

}
