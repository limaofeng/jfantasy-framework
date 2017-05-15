package org.jfantasy.member.service;

import org.jfantasy.member.MemberApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MemberApplication.class)
@ActiveProfiles("dev")
public class TeamMemberTest {
    @Autowired
    private TeamMemberService teamMemberService;
}
