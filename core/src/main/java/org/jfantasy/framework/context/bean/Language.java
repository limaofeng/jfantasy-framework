package org.jfantasy.framework.context.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

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
public class Language {
    @Id
    @Column(name = "ID", precision = 22)
    @GeneratedValue(generator = "fantasy-sequence")
    @GenericGenerator(name = "fantasy-sequence", strategy = "fantasy-sequence")
    private Long id;
    @Column(length = 2)
    private String locale;
    @Column(name = "MESSAGE_KEY")
    private String key;
    @Column(name = "MESSAGE_CONTENT")
    private String content;

}
