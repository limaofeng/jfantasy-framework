package org.jfantasy.pay.dao.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostUpdateEvent;
import org.jfantasy.framework.dao.hibernate.listener.AbstractChangedListener;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.enums.PaymentStatus;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by wei on 2017/3/19.
 */
@Component
public class PaymentChangedListener extends AbstractChangedListener<Payment> {

    private static final Log LOG = LogFactory.getLog(PaymentChangedListener.class);

    private PayService payService;

    public PaymentChangedListener() {
        super(EventType.POST_COMMIT_UPDATE);
    }

    @Override
    public void onPostUpdate(Payment payment,PostUpdateEvent event) {
        if (modify(event,"status")){
            if (payment.getStatus()== PaymentStatus.close){
                try {
                    payService.close(payment.getSn());
                } catch (PayException e) {
                    LOG.error(e.getMessage(),e);
                    throw new ValidationException(e.getMessage());
                }
            }
        }
    }
    @Autowired
    public void setPayService(PayService payService) {
        this.payService = payService;
    }
}
