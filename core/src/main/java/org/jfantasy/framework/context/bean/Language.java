package org.jfantasy.framework.context.bean;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

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
  @GeneratedValue(generator = "fantasy-sequence")
  @GenericGenerator(name = "fantasy-sequence", strategy = "fantasy-sequence")
  private Long id;

  @Column(length = 2)
  private String locale;

  @Column(name = "MESSAGE_KEY", length = 100)
  private String key;

  @Column(name = "MESSAGE_CONTENT", length = 512)
  private String content;
}
