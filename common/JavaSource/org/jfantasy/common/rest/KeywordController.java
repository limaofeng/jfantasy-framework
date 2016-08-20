package org.jfantasy.common.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.lucene.search.Query;
import org.jfantasy.common.bean.Keyword;
import org.jfantasy.common.service.KeywordService;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.lucene.BuguParser;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(value = "keyword", description = "关键词接口")
@RestController
@RequestMapping("/keywords")
public class KeywordController {

    @Autowired
    private KeywordService keywordService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<Keyword> search(@RequestParam(value = "type", required = false) String type, @RequestParam(value = "q", required = false) String query,Pager<Keyword> pager, List<PropertyFilter> filters) {
        List<Query> queries = new ArrayList<Query>();
        if(StringUtil.isNotBlank(query)){
            queries.add(BuguParser.parseWildcard(new String[]{"words","pinyin", "qpin"}, "*" + query + "*"));
        }
        if (StringUtil.isNotBlank(type)) {
            queries.add(BuguParser.parseTerm("type", type));
        }
        return this.keywordService.search(pager, BuguParser.parse(queries.toArray(new Query[queries.size()])));
    }

    @ApiOperation("查看关键词")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Keyword view(@PathVariable("id") Long id) {
        return this.keywordService.get(id);
    }

    @ApiOperation(value = "添加关键词", notes = "通过该接口, 添加关键词")
    @RequestMapping(method = {RequestMethod.POST})
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public Keyword create(@RequestBody Keyword keyword) {
        return this.keywordService.save(keyword.getType(), keyword.getWords());
    }

    @ApiOperation(value = "删除关键词", notes = "通过该接口, 删除关键词")
    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.keywordService.delete(id);
    }

    @ApiOperation(value = "批量删除关键词", notes = "通过该接口, 批量删除关键词")
    @RequestMapping(method = {RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody Long... id) {
        this.keywordService.delete(id);
    }

    @ApiOperation(value = "更新关键词", notes = "通过该接口, 更新关键词")
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public Keyword update(@PathVariable("id") Long id, @RequestBody Keyword keyword) {
        keyword.setId(id);
        return this.keywordService.save(keyword);
    }

}