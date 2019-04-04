package org.jfantasy.framework.dao;

import lombok.Builder;
import lombok.Data;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-03 14:01
 */
@Data
@Builder
public class OrderBy {
    private String by;
    private String order;

    @Override
    public String toString() {
        return by + "_" + order;
    }
}