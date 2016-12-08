package org.jfantasy.trade.bean.enums;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.trade.bean.Transaction;

public enum ProjectType {

    /**
     * 订单
     * 除了有订单信息外，必须有明确的付款方与收款方
     * 可用渠道：支付平台
     */
    order(AccountNotNull.FORM_OR_TO, TxChannel.internal, TxChannel.online),
    /**
     * 转账，必须有明确的付款方与收款方
     * 可用渠道：内部交易
     */
    transfer(AccountNotNull.FORM_OR_TO, TxChannel.internal),
    /**
     * 充值，无付款方
     * 可用渠道：内部交易／线下交易／会员卡／支付平台
     * 内部交易：自动操作
     * 线下交易：手动操作
     * 支付平台：交易成功
     */
    deposit(AccountNotNull.TO, TxChannel.internal, TxChannel.offline, TxChannel.card, TxChannel.online),
    /**
     * 提现，无收款方
     * 可用渠道：内部交易／线下交易／支付平台
     * 内部交易：自动操作
     * 线下交易：手动操作
     * 支付平台：交易成功
     */
    withdraw(AccountNotNull.FROM, TxChannel.internal, TxChannel.offline, TxChannel.online);

    private static final Log LOG = LogFactory.getLog(ProjectType.class);

    private AccountNotNull accountNotNull;
    private TxChannel[] channels;

    ProjectType(AccountNotNull notNull, TxChannel... channels) {
        this.accountNotNull = notNull;
        this.channels = channels;
    }

    enum AccountNotNull {
        FROM, TO, FORM_OR_TO
    }

    public void verify(Transaction transaction) {
        switch (this.accountNotNull) {
            case FORM_OR_TO:
                if (StringUtil.isBlank(transaction.getFrom()) || StringUtil.isBlank(transaction.getTo())) {
                    throw new ValidationException("付款方与收款方不能为空");
                }
                break;
            case FROM:
                if (StringUtil.isBlank(transaction.getFrom()) || StringUtil.isNotBlank(transaction.getTo())) {
                    throw new ValidationException("付款方不能为空 - 收款方只能为空");
                }
            case TO:
                if (StringUtil.isNotBlank(transaction.getFrom()) || StringUtil.isBlank(transaction.getTo())) {
                    throw new ValidationException("付款方只能为空 - 收款方不能为空");
                }
                break;
            default:
        }
        if (transaction.getChannel() != null && !ObjectUtil.exists(this.channels, transaction.getChannel())) {
            throw new ValidationException("渠道错误");
        }
    }

}
