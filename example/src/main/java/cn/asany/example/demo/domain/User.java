package cn.asany.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.dao.hibernate.annotations.TableGenerator;
import org.jfantasy.framework.search.annotations.IndexProperty;
import org.jfantasy.framework.search.annotations.Indexed;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-03-15 15:38
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Indexed(indexName = "users")
@Table(name = "SYS_USER")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User extends BaseBusEntity {
  @Id
  @Column(name = "ID", nullable = false, updatable = false, precision = 22)
  @TableGenerator(allocationSize = 5)
  private Long id;

  @IndexProperty
  @Column(name = "USERNAME", length = 18)
  private String username;

  @Column(name = "password", length = 21)
  private String password;
}
