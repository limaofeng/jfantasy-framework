package org.jfantasy.pay.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.order.bean.Order;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.enums.PaymentStatus;
import org.jfantasy.pay.bean.enums.PaymentType;
import org.jfantasy.pay.dao.PaymentDao;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.product.PayProduct;
import org.jfantasy.trade.bean.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


/**
 * 支付service
 */
@Service
@Transactional
public class PaymentService {

    private static final Log LOG = LogFactory.getLog(PaymentService.class);

    private final PaymentDao paymentDao;
    private PayConfigService payConfigService;

    @Autowired
    public PaymentService(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    /**
     * 支付准备
     *
     * @param order      订单信息
     * @param payConfig  支付配置
     * @param payProduct 支付产品
     * @param payer      付款人
     * @return Payment
     * @throws PayException 支付异常
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Payment create(Transaction transaction, Order order, PayConfig payConfig, PayProduct payProduct, String payer) throws PayException {
        //在线支付
        if (PayConfig.PayConfigType.online != payConfig.getPayConfigType()) {
            throw new ValidationException(100000, "暂时只支持在线支付");
        }
        //判断交易历史
        List<Payment> payments = this.paymentDao.find(Restrictions.eq("transaction.sn", transaction.getSn()));
        if (ObjectUtil.exists(payments, "status", PaymentStatus.success)) {
            throw new ValidationException(100000, "交易已经支付成功,请勿反复支付");
        }
        // 如果存在完成订单
        Payment payment = ObjectUtil.find(payments, "payConfig.id", payConfig.getId());
        if (payment != null) {//如果存在未完成的支付信息
            return payment;
        }
        //支付配置类型（线下支付、在线支付）
        PaymentType paymentType = PaymentType.online;
        BigDecimal paymentFee = BigDecimal.ZERO; //支付手续费
        BigDecimal amountPayable = order.getPayableAmount();//应付金额（含支付手续费）
        //保存交易
        payment = new Payment();
        String bankName = payProduct.getName();
        String bankAccount = payConfig.getBargainorId();
        payment.setType(paymentType);
        payment.setPayConfigName(payConfig.getName());
        payment.setBankName(bankName);
        payment.setBankAccount(bankAccount);
        payment.setTotalAmount(amountPayable);
        payment.setPaymentFee(paymentFee);
        payment.setPayer(payer);
        payment.setMemo(null);
        payment.setStatus(PaymentStatus.ready);
        payment.setPayConfig(payConfig);
        payment.setOrder(order);
        payment.setTransaction(transaction);
        payment = this.paymentDao.save(payment);
        //保存交易日志
//        paymentLogDao.save(payment, "创建" + payment.getPayConfigName() + " 交易");
        return payment;
    }

    public List<Payment> find(Criterion... criterions) {
        return this.paymentDao.find(criterions);
    }

    /**
     * 支付结果
     *
     * @param payment 支付对象
     */
    public void save(Payment payment) {
        this.paymentDao.save(payment);
    }

    /**
     * 过期支付单
     *
     * @param sn 支付编号
     */
    public void close(String sn) {
        this.close(sn, null);
    }

    public void close(String sn, String tradeNo) {
        Payment payment = get(sn);
        payment.setStatus(PaymentStatus.close);
        if (tradeNo != null) {
            payment.setTradeNo(tradeNo);
        }
        this.paymentDao.save(payment);
    }


    public List<Payment> find(List<PropertyFilter> filters, String orderBy, String order) {
        return this.paymentDao.find(filters, orderBy, order);
    }

    public PayConfig getPaymentConfig(Long id) {
        return this.payConfigService.get(id);
    }

    public Pager<Payment> findPager(Pager<Payment> pager, List<PropertyFilter> filters) {
        return paymentDao.findPager(pager, filters);
    }

    public Payment get(String sn) {
        Payment payment = this.paymentDao.get(sn);
        if (payment == null) {
            throw new NotFoundException("[SN=" + sn + "]对应的支付记录未找到");
        }
        return payment;
    }

    public void delete(String... sns) {
        for (String sn : sns) {
            this.paymentDao.delete(sn);
        }
    }

    @Autowired
    public void setPayConfigService(PayConfigService payConfigService) {
        this.payConfigService = payConfigService;
    }

}
