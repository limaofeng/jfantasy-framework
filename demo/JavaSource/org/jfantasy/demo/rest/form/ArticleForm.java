package org.jfantasy.demo.rest.form;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.jfantasy.demo.bean.Article;

public class ArticleForm {

    private Article article;

    @JsonCreator
    public ArticleForm(Article article) {//@JsonProperty("article")
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }

}
