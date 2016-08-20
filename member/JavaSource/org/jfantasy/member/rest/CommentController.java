package org.jfantasy.member.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.member.bean.Comment;
import org.jfantasy.member.bean.Member;
import org.jfantasy.member.rest.models.assembler.CommentResourceAssembler;
import org.jfantasy.member.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "comments", description = "评论")
@RestController
@RequestMapping("/comments")
public class CommentController {

    private CommentResourceAssembler assembler = new CommentResourceAssembler();

    @Autowired
    private CommentService commentService;

    @JsonResultFilter(allow = @AllowProperty(pojo = Member.class, name = {"id", "nick_name"}))
    @ApiOperation(value = "查询评论", notes = "返回会员的会员评论")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<ResultResourceSupport> search(Pager<Comment> pager, List<PropertyFilter> filters) {
        filters.add(new PropertyFilter("NULL_forComment"));
        return assembler.toResources(this.commentService.findPager(pager, filters));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResultResourceSupport save(@RequestBody Comment comment) {
        comment.setIp("");
        comment.setUsername("username");
        comment.setShow(true);
        comment.setMember(null);
        return assembler.toResource(this.commentService.save(comment));
    }

    @ApiOperation(value = "删除评论", notes = "删除会员会员评论")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.commentService.deltele(id);
    }

    protected Comment get(Long id) {
        return this.commentService.get(id);
    }
}