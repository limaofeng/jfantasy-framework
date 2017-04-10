package org.jfantasy.member.service;

import org.jfantasy.member.MemberApplication;
import org.jfantasy.member.bean.Comment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MemberApplication.class)
@ActiveProfiles("dev")
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    @Test
    public void save() throws Exception {
        Comment comment = new Comment();
        comment.setTargetId("8aaac6ef5938f14f01593a200fe2003d");
        comment.setContent("测试测试的点点滴滴");
        comment.setTargetType("doctor");
        comment.setMemberId(302l);
        commentService.save(comment);
    }

}