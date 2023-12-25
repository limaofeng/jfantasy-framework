package cn.asany.example.demo.domain;

import cn.asany.example.demo.validator.CaseValidator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import net.asany.jfantasy.framework.dao.BaseBusEntity;
import net.asany.jfantasy.framework.dao.hibernate.annotations.TableGenerator;
import net.asany.jfantasy.framework.search.annotations.IndexProperty;
import net.asany.jfantasy.framework.search.annotations.Indexed;
import net.asany.jfantasy.framework.spring.validation.Use;

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
  @NotBlank(message = "用户名不能为空")
  @Use(value = CaseValidator.class, message = "自定义的错误消息")
  @Column(name = "USERNAME", length = 18)
  private String username;

  @NotBlank(message = "密码不能为空")
  @Column(name = "password", length = 21)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 10)
  private UserStatus status;

  private transient UserSetting setting = new UserSetting();
}
