package cn.asany.demo.domain;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;
import lombok.*;
import net.asany.jfantasy.framework.search.annotations.FieldType;
import net.asany.jfantasy.framework.search.annotations.IndexProperty;
import net.asany.jfantasy.framework.search.annotations.Indexed;

@Indexed(indexName = "articles")
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

  @IndexProperty @Column private String title;

  @Column
  @IndexProperty(type = FieldType.Keyword)
  private String author;

  @Column
  @IndexProperty(type = FieldType.Keyword)
  private String url;

  @Column @IndexProperty private String content;

  @Column @IndexProperty private Date publishDate;

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
