package org.jfantasy.pay.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.BeanUtil;
import org.jfantasy.framework.util.common.NumberUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.entity.enums.OrderStatus;
import org.jfantasy.order.service.OrderService;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.bean.enums.PayMethod;
import org.jfantasy.pay.bean.enums.PaymentStatus;
import org.jfantasy.pay.bean.enums.PaymentType;
import org.jfantasy.pay.bean.enums.RefundStatus;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.product.PayProduct;
import org.jfantasy.pay.product.PayType;
import org.jfantasy.pay.product.Walletpay;
import org.jfantasy.pay.service.vo.ToPayment;
import org.jfantasy.pay.service.vo.ToRefund;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.ProjectType;
import org.jfantasy.trade.bean.enums.TxChannel;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.trade.service.ProjectService;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

/**
 * 支付服务
 */
@Service
public class PayService {

    private static final Log LOG = LogFactory.getLog(PayService.class);

    private final ProjectService projectService;
    private final PayProductConfiguration payProductConfiguration;
    private final PayConfigService payConfigService;
    private final PaymentService paymentService;
    private final RefundService refundService;
    private final OrderService orderService;
    private final TransactionService transactionService;

    @Autowired
    public PayService(ProjectService projectService, RefundService refundService, PaymentService paymentService, PayProductConfiguration payProductConfiguration, PayConfigService payConfigService, OrderService orderService, TransactionService transactionService) {
        this.projectService = projectService;
        this.refundService = refundService;
        this.paymentService = paymentService;
        this.payProductConfiguration = payProductConfiguration;
        this.payConfigService = payConfigService;
        this.orderService = orderService;
        this.transactionService = transactionService;
    }

    /**
     * 生成预支付单与web支付表单
     *
     * @param transaction 交易对象
     * @param payConfigId 支付ID
     * @param payType     支付类型
     * @param payer       支付人
     * @param properties  支付参数
     * @return ToPayment
     * @throws PayException 支付异常
     */
    @Transactional
    public ToPayment pay(Transaction transaction, Long payConfigId, PayType payType, String payer, Properties properties) throws PayException {
        boolean paySuccess = false;
        for (Payment payment : ObjectUtil.filter(transaction.getPayments(), "status", PaymentStatus.ready)) {
            payment = this.query(payment.getSn());
            if (!paySuccess && payment.getStatus() == PaymentStatus.success) {
                paySuccess = true;
            }
        }
        if (paySuccess) {
            throw new ValidationException(100000, "订单已支付成功");
        }
        LOG.debug("开始付款");
        Project project = this.projectService.get(transaction.getProject());
        if (project.getType() != ProjectType.order) {
            throw new ValidationException(100000, "项目类型为 order 才能调用支付接口");
        }
        // 设置 Trx 到 properties 中
        properties.put(Walletpay.PROPERTY_TRANSACTION, transaction);
        //获取订单信息
        String orderId = transaction.get(Transaction.ORDER_ID);
        //验证业务订单
        if (transaction.getStatus() != TxStatus.unprocessed) {
            throw new ValidationException(100000, "交易状态为[" + transaction.getStatus() + "],不满足付款的必要条件");
        }
        Order order = orderService.get(orderId);
        if (order.getStatus() != OrderStatus.unpaid) {
            throw new ValidationException(100000, "订单状态为[" + order.getStatus() + "],不满足付款的必要条件");
        }
        if (order.isExpired()) {//这里有访问数据库操作,所以放在后面
            throw new ValidationException(100000, "订单超出支付期限，不能进行支付");
        }
        //获取支付配置
        PayConfig payConfig = payConfigService.get(payConfigId);
        if (payConfig.getPayMethod() == PayMethod.thirdparty && NumberUtil.isEquals(BigDecimal.ZERO, transaction.getAmount())) {
            throw new ValidationException("第三方支付平台不支持0元支付");
        }
        //获取支付产品
        PayProduct payProduct = payProductConfiguration.loadPayProduct(payConfig.getPayProductId());
        //开始支付,创建支付记录
        Payment payment = paymentService.create(transaction, order, payConfig, payProduct, payer);
        //克隆返回结果
        ToPayment toPayment = new ToPayment();
        BeanUtil.copyProperties(toPayment, payment, "status", "type");
        toPayment.setStatus(payment.getStatus());
        toPayment.setType(payment.getType());
        //设置交易的渠道
        transaction.setPayConfigName(payConfig.getName());
        transaction.setChannel(payConfig.getPayMethod() == PayMethod.thirdparty ? TxChannel.online : TxChannel.internal);
        transactionService.update(transaction);
        //调用第三方支付产品
        if (PayType.web == payType) {
            toPayment.setSource(payProduct.web(payment, order, properties));
        } else if (PayType.app == payType) {
            toPayment.setSource(payProduct.app(payment, order, properties));
        } else if (PayType.wap == payType) {
            toPayment.setSource(payProduct.wap(payment,order,properties));
        }
        if (payConfig.getPayMethod() == PayMethod.thirdparty) {
            //有可能调用支付产品时,修改了支付状态,保存支付信息
            paymentService.save(payment);
        }
        return toPayment;
    }

    /**
     * 发起退款交易
     *
     * @param transaction 交易
     */
    @Transactional
    public Refund refund(Transaction transaction) {
        String id = transaction.get(Transaction.ORDER_ID);
        List<Payment> payments = paymentService.find(Restrictions.eq("order.id", id));
        Payment payment = ObjectUtil.find(payments, "status", PaymentStatus.success);
        return refundService.create(payment, transaction.getAmount(), transaction, "退款");
    }

    /**
     * 更新退款状态 <br/>
     * 对应的业务操作: <br/>
     * ready => close
     * ready => wait
     *
     * @param sn     退款编号
     * @param status 状态
     * @param remark 备注
     * @return Refund
     */
    @Transactional
    public ToRefund refund(String sn, RefundStatus status, String remark) throws PayException {
        Refund refund = refundService.get(sn);
        Transaction transaction = refund.getTransaction();

        if (refund.getType() == PaymentType.online) {
            Order order = refund.getOrder();
            if (order.getStatus() != OrderStatus.refunding) {
                throw new ValidationException(100000, "订单状态为[" + order.getStatus() + "],不满足付款的必要条件");
            }
            if (refund.getStatus() != RefundStatus.ready) {
                throw new PayException("退款状态为:" + refund.getStatus() + ",不能进行操作");
            } else if (!ObjectUtil.exists(new RefundStatus[]{RefundStatus.close, RefundStatus.wait}, status)) {
                throw new PayException("不能手动将退款状态调整为:" + status);
            }
            if (status == RefundStatus.wait) {
                PayConfig payConfig = refund.getPayConfig();
                //获取支付产品
                PayProduct payProduct = payProductConfiguration.loadPayProduct(payConfig.getPayProductId());
                //设置交易的渠道
                transaction.setPayConfigName(payConfig.getName());
                transactionService.update(transaction);
                Object result = payProduct.refund(refund);
                this.refundService.save(refund);
                ToRefund toRefund = BeanUtil.copyProperties(new ToRefund(), refund);
                toRefund.setSource(result);
                return toRefund;
            } else if (status == RefundStatus.close) {
                refund.setStatus(status);
                this.refundService.save(refund);
                return BeanUtil.copyProperties(new ToRefund(), refund);
            } else {
                throw new PayException(" 变更退款状态到 " + status + "功能,暂未实现! 请联系技术人员. ");
            }
        } else {
            throw new PayException(" 线下退款方式,暂未实现! 请联系技术人员. ");
        }
    }

    public Payment query(String sn) throws PayException {
        Payment payment = this.paymentService.get(sn);
        PayConfig payConfig = payment.getPayConfig();
        //获取支付产品
        PayProduct payProduct = payProductConfiguration.loadPayProduct(payConfig.getPayProductId());

        PaymentStatus oldStatus = payment.getStatus();

        payProduct.query(payment);

        //支付状态发生变化
        if (payment.getStatus() != oldStatus) {
            this.update(payment);
        }

        return payment;
    }

    /**
     * 调用支付产品接口关闭支付订单
     * @param sn
     */
    @Transactional
    public void close(String sn) throws PayException {
        Payment payment = this.paymentService.get(sn);
        PayConfig payConfig = payment.getPayConfig();
        //获取支付产品
        PayProduct payProduct = payProductConfiguration.loadPayProduct(payConfig.getPayProductId());
        payProduct.close(payment);
    }

    /**
     * 支付通知
     *
     * @param sn   支付对象
     * @param body 请求字符串
     * @return Object
     */
    @Transactional
    public Object paymentNotify(String sn, String body) throws PayException {
        Payment payment = this.paymentService.get(sn);
        PayConfig payConfig = payment.getPayConfig();

        //获取支付产品
        PayProduct payProduct = payProductConfiguration.loadPayProduct(payConfig.getPayProductId());

        //支付订单
        Order order = payment.getOrder();

        PaymentStatus oldStatus = payment.getStatus();

        Object result = payProduct.payNotify(payment, body);

        //支付状态发生变化
        if (payment.getStatus() != oldStatus) {
            this.update(payment);
        }

        //返回订单信息
        return result != null ? result : order;
    }

    public void update(Payment payment) {
        // 更新支付状态
        paymentService.save(payment);
        // 更新订单信息
        switch (this.orderService.paySuccess(payment)) {
            case 1:
                //关闭其他支付记录
                String orderId = payment.getOrderId();
                List<Payment> payments = paymentService.find(Restrictions.eq("order.id", orderId));
                if (payments.size()>1){
                    for (Payment payment1:payments){
                        if (payment1.getSn()!=payment.getSn()){
                            payment1.setStatus(PaymentStatus.close);
                            paymentService.save(payment1);
                        }
                    }
                }
                break;
            case 0:
                //  TODO 记录异常信息
                break;
            default:
        }
    }

    /**
     * 退款通知
     *
     * @param sn   退款订单
     * @param body 请求字符串
     * @return Object
     */
    @Transactional
    public Object refundNotify(String sn, String body) throws PayException {
        Refund refund = this.refundService.get(sn);
        PayConfig payConfig = refund.getPayConfig();

        //获取支付产品
        PayProduct payProduct = payProductConfiguration.loadPayProduct(payConfig.getPayProductId());

        //支付订单
        Order order = refund.getOrder();

        RefundStatus oldStatus = refund.getStatus();

        Object result = payProduct.payNotify(refund, body);

        //状态未发生变化
        if (refund.getStatus() == oldStatus) {
            return result != null ? result : order;
        }

        if (refund.getStatus() == RefundStatus.success) {
            this.orderService.updateRefundStatus(refund);
        }

        // 如果为完成 或者 初始状态 不触发事件
        if (refund.getStatus() == RefundStatus.wait || refund.getStatus() == RefundStatus.ready) {
            return result == null ? order : result;
        }

        //返回订单信息
        return result != null ? result : order;
    }

}