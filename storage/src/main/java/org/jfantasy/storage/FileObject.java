package org.jfantasy.storage;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-09 18:15
 */
@Data
public class FileObject implements Serializable {
  private String id;
  private String md5;
  private String name;
  private String path;
  private String mimeType;
  private Long length;
  private String destination;
  private Date createdAt;
  private Date updatedAt;
}
