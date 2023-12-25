package net.asany.jfantasy.framework.context.bean;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.asany.jfantasy.framework.dao.hibernate.annotations.TableGenerator;

/**
 * 国际化
 *
 * @author limaofeng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SYS_LANGUAGE")
public class Language implements Serializable {
  @Id
  @Column(name = "ID", precision = 22)
  @TableGenerator
  private Long id;

  @Column(length = 2)
  private String locale;

  @Column(name = "MESSAGE_KEY", length = 100)
  private String key;

  @Column(name = "MESSAGE_CONTENT", length = 512)
  private String content;
}
