package org.jfantasy.framework.security.authentication;

import lombok.Data;
import org.jfantasy.framework.security.LoginUser;
import org.jfantasy.framework.security.core.GrantedAuthority;
import org.jfantasy.framework.security.core.authority.AuthorityUtils;

import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author limaofeng
 */
@Data
public abstract class AbstractAuthenticationToken implements Authentication {
    private String name;
    private Object details;
    private Collection<GrantedAuthority> authorities;
    private boolean authenticated;

    public AbstractAuthenticationToken(Collection authorities) {
        if (authorities == null) {
            this.authorities = AuthorityUtils.NO_AUTHORITIES;
            return;
        }
        this.authorities = Collections.unmodifiableList(new ArrayList<>(authorities));
    }

}
