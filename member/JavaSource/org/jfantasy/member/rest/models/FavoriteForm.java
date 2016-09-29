package org.jfantasy.member.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.member.bean.Favorite;

import javax.validation.constraints.NotNull;

public class FavoriteForm {
    @NotNull(groups = RESTful.POST.class)
    private Long memberId;
    @NotNull(groups = RESTful.POST.class)
    private String type;
    @NotNull(groups = RESTful.POST.class)
    private String targetType;
    @NotNull(groups = RESTful.POST.class)
    private String targetId;
    @NotNull(groups = RESTful.POST.class)
    private boolean watch;

    public FavoriteForm() {
    }

    public FavoriteForm(Long memberId, String type, String targetType, String targetId, boolean watch) {
        this.memberId = memberId;
        this.type = type;
        this.targetType = targetType;
        this.targetId = targetId;
        this.watch = watch;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public boolean isWatch() {
        return watch;
    }

    public void setWatch(boolean watch) {
        this.watch = watch;
    }

    @JsonIgnore
    public Favorite toFavorite() {
        Favorite favorite = new Favorite();
        favorite.setType(type);
        favorite.setMemberId(memberId);
        favorite.setTargetId(targetId);
        favorite.setTargetType(targetType);
        return favorite;
    }
}
