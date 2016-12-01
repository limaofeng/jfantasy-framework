package org.jfantasy.pay.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.BeanUtil;
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
        LOG.debug("开始付款");
        Project project = this.projectService.get(transaction.getProject());
        if (project.getType() != ProjectType.order) {
            throw new ValidationException(000.0f, "项目类型为 order 才能调用支付接口");
        }
        // 设置 Trx 到 properties 中
        properties.put(Walletpay.PROPERTY_TRANSACTION, transaction);
        //获取订单信息
        String orderId = transaction.get(Transaction.ORDER_ID);
        //验证业务订单
        if (transaction.getStatus() != TxStatus.unprocessed) {
            throw new ValidationException(000.0f, "交易状态为[" + transaction.getStatus() + "],不满足付款的必要条件");
        }
        Order order = orderService.get(orderId);
        if (order.getStatus() != OrderStatus.unpaid) {
            throw new ValidationException(000.0f, "订单状态为[" + order.getStatus() + "],不满足付款的必要条件");
        }
        if (order.isExpired()) {//这里有访问数据库操作,所以放在后面
            throw new ValidationException(000.0f, "订单超出支付期限，不能进行支付");
        }
        //获取支付配置
        PayConfig payConfig = payConfigService.get(payConfigId);
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
        transaction.setChannel(payConfig.getPayMethod() == PayMethod.thirdparty ? TxChannel.thirdparty : TxChannel.internal);
        transaction.setPayConfigName(payConfig.getName());
        transactionService.update(transaction);
        //调用第三方支付产品
        if (PayType.web == payType) {
            toPayment.setSource(payProduct.web(payment, order, properties));
        } else if (PayType.app == payType) {
            toPayment.setSource(payProduct.app(payment, order, properties));
        }
        if (payConfig.getPayMethod() == PayMethod.thirdparty) {
            //有可能调用支付产品时,修改了支付状态,保存支付信息
            paymentService.save(payment);
        }
        return toPayment;
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
            if (order.getStatus() != OrderStatus.paid) {
                throw new ValidationException(000.0f, "订单状态为[" + order.getStatus() + "],不满足付款的必要条件");
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
                transaction.setChannel(payConfig.getPayMethod() == PayMethod.thirdparty ? TxChannel.thirdparty : TxChannel.internal);
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

    public boolean query(String sn) throws PayException {
        Payment payment = this.paymentService.get(sn);
        PayConfig payConfig = payment.getPayConfig();
        //获取支付产品
        PayProduct payProduct = payProductConfiguration.loadPayProduct(payConfig.getPayProductId());
        payProduct.query(payment);
        return false;
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
        Transaction transaction = payment.getTransaction();
        PayConfig payConfig = payment.getPayConfig();

        //获取支付产品
        PayProduct payProduct = payProductConfiguration.loadPayProduct(payConfig.getPayProductId());

        //支付订单
        Order order = payment.getOrder();

        PaymentStatus oldStatus = payment.getStatus();

        Object result = payProduct.payNotify(payment, body);

        //支付状态未发生变化
        if (payment.getStatus() == oldStatus) {
            return result == null ? order : result;
        }

        // 更新支付状态
        paymentService.save(payment);

        // 更新订单信息
        if (payment.getStatus() == PaymentStatus.success) {
            // 更新交易状态
            transaction.setChannel(payConfig.getPayMethod() == PayMethod.thirdparty ? TxChannel.thirdparty : TxChannel.internal);
            transaction.setStatus(TxStatus.success);
            transaction.setStatusText(TxStatus.success.getValue());
            transaction.setPayConfigName(payConfig.getName());
            transactionService.update(transaction);
            // 更新订单状态
            order.setStatus(OrderStatus.paid);
            order.setPaymentStatus(org.jfantasy.order.entity.enums.PaymentStatus.paid);
            order.setPaymentTime(payment.getTradeTime());
            order.setPaymentConfig(payConfig);
            order.setPayConfigName(payConfig.getName());
            orderService.update(order);
        }

        // 如果为完成 或者 初始状态 不触发事件
        if (payment.getStatus() == PaymentStatus.finished || payment.getStatus() == PaymentStatus.ready) {
            return result == null ? order : result;
        }

        //返回订单信息
        return result != null ? result : order;
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
        Transaction transaction = refund.getTransaction();

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

        //更新状态
        refundService.result(refund, order);


        if (refund.getStatus() == RefundStatus.success) {
            // 更新交易状态
            transaction.setChannel(payConfig.getPayMethod() == PayMethod.thirdparty ? TxChannel.thirdparty : TxChannel.internal);
            transaction.setStatus(TxStatus.success);
            transaction.setStatusText(TxStatus.success.getValue());
            transaction.setPayConfigName(payConfig.getName());
            transactionService.update(transaction);
            // 更新订单状态
            order.setStatus(OrderStatus.refunded);
            order.setPaymentStatus(order.getPayableAmount().equals(refund.getTotalAmount()) ? org.jfantasy.order.entity.enums.PaymentStatus.refunded : org.jfantasy.order.entity.enums.PaymentStatus.partRefund);
            order.setRefundAmount(refund.getTotalAmount());
            order.setRefundTime(refund.getTradeTime());
            orderService.update(order);
        }

        // 如果为完成 或者 初始状态 不触发事件
        if (refund.getStatus() == RefundStatus.wait || refund.getStatus() == RefundStatus.ready) {
            return result == null ? order : result;
        }

        //返回订单信息
        return result != null ? result : order;
    }

}