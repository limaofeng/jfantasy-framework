package cn.asany.demo.bean;

import java.util.Date;
import java.util.Objects;
import javax.persistence.*;
import lombok.*;
import org.jfantasy.framework.search.annotations.Document;
import org.jfantasy.framework.search.annotations.Field;
import org.jfantasy.framework.search.annotations.FieldType;

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

  @Id @GeneratedValue private Long id;

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
