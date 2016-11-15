package org.jfantasy.security.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jfantasy.framework.service.PasswordTokenType;

public class PasswordForm {

    private PasswordTokenType type = PasswordTokenType.password;
    @JsonProperty("old_password")
    private String oldPassword;
    @JsonProperty("new_password")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public PasswordTokenType getType() {
        return type;
    }

    public void setType(PasswordTokenType type) {
        this.type = type;
    }
}
