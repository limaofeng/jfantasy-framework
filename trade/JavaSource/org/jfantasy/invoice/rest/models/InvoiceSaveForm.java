package org.jfantasy.invoice.rest.models;

import org.hibernate.validator.constraints.Length;
import org.jfantasy.framework.spring.validation.RESTful;

import javax.validation.constraints.NotNull;

public class InvoiceSaveForm {
    
    @NotNull(groups = RESTful.POST.class)
    private Long memberId;
    @NotNull(groups = RESTful.POST.class)
    private Long receiverId;
    @NotNull(groups = RESTful.POST.class)
    @Length(min = 1, groups = RESTful.POST.class)
    private String[] items;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }
}
