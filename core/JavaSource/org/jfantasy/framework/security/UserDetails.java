package org.jfantasy.framework.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-02 13:28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails<T> {
    private String uid;
    private String username;
    private String displayName;
    private T profile;
}
