package cn.asany.demo.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.jfantasy.framework.search.annotations.Document;
import org.jfantasy.framework.search.annotations.Field;
import org.jfantasy.framework.search.annotations.FieldType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Document(indexName = "articles")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "cms_article")
@Entity
public class Article {

  @Id
  private Long id;

  @Field @Column private String title;

  @Column
  @Field(type = FieldType.Keyword)
  private String author;

  @Column
  @Field(type = FieldType.Keyword)
  private String url;

  @Column @Field private String content;

  @Column @Field private Date publishDate;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || org.hibernate.Hibernate.getClass(this) != org.hibernate.Hibernate.getClass(o))
      return false;
    Article article = (Article) o;
    return id != null && Objects.equals(id, article.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
