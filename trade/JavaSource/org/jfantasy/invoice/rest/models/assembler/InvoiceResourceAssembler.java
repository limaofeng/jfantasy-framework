package org.jfantasy.invoice.rest.models.assembler;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.invoice.bean.Invoice;
import org.jfantasy.invoice.rest.InvoiceController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class InvoiceResourceAssembler  extends ResourceAssemblerSupport<Invoice, ResultResourceSupport> {

    public InvoiceResourceAssembler() {
        super(InvoiceController.class, ResultResourceSupport.class);
    }

    @Override
    protected ResultResourceSupport<Invoice> instantiateResource(Invoice entity) {
        return new ResultResourceSupport<>(entity);
    }

    @Override
    public ResultResourceSupport toResource(Invoice entity) {
        return createResourceWithId(entity.getId(), entity);
    }

    public Pager<ResultResourceSupport> toResources(Pager<Invoice> pager) {
        return new Pager<>(pager,this.toResources(pager.getPageItems()));
    }

}
