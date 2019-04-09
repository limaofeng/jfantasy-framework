package org.jfantasy.graphql;

import lombok.Data;

import java.util.Date;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-08 16:29
 */
@Data
public class FileObject {
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
