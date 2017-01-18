package org.jfantasy.pay.product;

import org.jfantasy.framework.httpclient.HttpClientUtil;
import org.jfantasy.framework.httpclient.Response;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.util.HandlebarsTemplateUtils;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.framework.util.web.WebUtil;
import org.jfantasy.order.bean.Order;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.bean.enums.PaymentStatus;
import org.jfantasy.pay.bean.enums.RefundStatus;
import org.jfantasy.pay.product.sign.MD5;
import org.jfantasy.pay.product.sign.RSA;
import org.jfantasy.pay.product.sign.SignUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 支付宝（即时交易）
 */
public class Alipay extends PayProductSupport {
    /**
     * 支付宝消息验证地址
     */
    private static final String INPUT_CHARSET = "utf-8";
    private static final DecimalFormat RMB_YUAN_FORMAT = new DecimalFormat("#0.00");
    public static final String EXT_SELLER_EMAIL = "sellerEmail";
    private static final String HTTPS_VERIFY_URL = "https://mapi.alipay.com/gateway.do?service=notify_verify&";
    private static final String REGEXP_PARSE = "^\\{(.*)\\}$";

    private static final Urls urls = new Urls();

    static {
        urls.paymentUrl = "https://mapi.alipay.com/gateway.do?_input_charset=" + INPUT_CHARSET;// 支付请求URL
        urls.refundUrl = "https://mapi.alipay.com/gateway.do";//统一收单交易退款接口
        urls.queryUrl = "https://openapi.alipay.com/gateway.do";
    }

    @Override
    public String web(Payment payment, Order order, Properties properties) throws PayException {
        PayConfig config = payment.getPayConfig();
        final Map<String, String> data = new TreeMap<>();
        try {
            // 常规参数
            data.put("service", "create_direct_pay_by_user");// 接口类型（create_direct_pay_by_user：即时交易）
            data.put("_input_charset", INPUT_CHARSET);//字符编码格式
            data.put("payment_type", "1");// 支付类型（固定值：1）
            data.put("notify_url", paySettings.getUrl() + "/pays/" + payment.getSn() + "/notify");// 消息通知URL

            //data.put("anti_phishing_key", "");防钓鱼时间戳
            //data.put("exter_invoke_ip", "");客户端的IP地址
            data.put("it_b_pay", properties.getProperty("it_b_pay", order.getExpires() + "m"));//未付款交易的超时时间 设置未付款交易的超时时间，一旦超时，该笔交易就会自动被关闭。当用户输入支付密码、点击确认付款后（即创建支付宝交易后）开始计时。取值范围：1m～15d，或者使用绝对时间（示例格式：2014-06-13 16:00:00）。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。该参数数值不接受小数点，如1.5h，可转换为90m。

            data.put("partner", config.getBargainorId());// 合作身份者ID
            data.put("seller_id", config.getBargainorId());// 商家ID

            data.put("out_trade_no", payment.getSn());// 支付编号
            data.put("subject", order.getSubject());// 订单的名称、标题、关键字等
            data.put("body", order.getBody());// 订单描述
            data.put("total_fee", RMB_YUAN_FORMAT.format(payment.getTotalAmount()));// 总金额（单位：元）

            //额外参数
            if (StringUtil.isNotBlank(properties.getProperty(PROPERTIES_BACKURL))) {
                data.put("return_url", properties.getProperty(PROPERTIES_BACKURL));//同步通知
                LOG.debug("添加参数 return_url = " + properties.getProperty("return_url"));
            }
            if (StringUtil.isNotBlank(properties.getProperty(PROPERTIES_SHOWURL))) {
                data.put("show_url", properties.getProperty(PROPERTIES_SHOWURL));// 商品显示URL
                LOG.debug("添加参数 show_url = " + properties.getProperty("show_url"));
            }
            if (StringUtil.isNotBlank(properties.getProperty("extra_common_param"))) {
                data.put("extra_common_param", properties.getProperty("extra_common_param"));
                LOG.debug("添加参数 extra_common_param = " + properties.getProperty("extra_common_param"));
            }
            if (StringUtil.isNotBlank(properties.getProperty("bank_no"))) {
                data.put("defaultbank", properties.getProperty("bank_no"));// 默认选择银行（当paymethod为bankPay时有效）
                LOG.debug("添加参数 defaultbank = " + properties.getProperty("defaultbank"));
                data.put("paymethod", "bankPay");// 默认支付方式（bankPay：网银、cartoon：卡通、directPay：余额、CASH：网点支付）
            } else {
                data.put("paymethod", "directPay");
            }

            // 参数处理
            data.put("sign_type", "MD5");
            data.put("sign", sign(data, data.get("sign_type"),config.getBargainorKey()));

            data.put("subject", WebUtil.transformCoding(order.getSubject(), INPUT_CHARSET, INPUT_CHARSET));// 订单的名称、标题、关键字等
            data.put("body", WebUtil.transformCoding(order.getBody(), INPUT_CHARSET, INPUT_CHARSET));// 订单描述

            Map<String, Object> tdata = new HashMap<>();
            tdata.put("url", urls.paymentUrl);
            tdata.put("params", data.entrySet());
            //拼接支付表单
            return HandlebarsTemplateUtils.processTemplateIntoString(HandlebarsTemplateUtils.template("/org.jfantasy.pay.product.template/pay"), tdata);
        } catch (IOException e) {
            LOG.error(e);
            throw new PayException("支付过程发生错误，错误信息为:" + e.getMessage());
        }
    }

    @Override
    public Object app(Payment payment, Order order, Properties properties) throws PayException {
        PayConfig config = payment.getPayConfig();

        Map<String, String> data = new TreeMap<>();
        data.put("service", "mobile.securitypay.pay");//接口名称
        data.put("partner", config.getBargainorId());//合作者身份ID
        data.put("_input_charset", INPUT_CHARSET);//参数编码字符集
        data.put("notify_url", paySettings.getUrl() + "/pays/" + payment.getSn() + "/notify");//服务器异步通知页面路径

        data.put("out_trade_no", payment.getSn());//商户网站唯一订单号
        data.put("subject", order.getSubject());//商品名称
        data.put("payment_type", "1");//支付类型
        data.put("seller_id", config.get(EXT_SELLER_EMAIL, String.class));//卖家支付宝账号
        data.put("total_fee", RMB_YUAN_FORMAT.format(order.getPayableAmount()));//总金额
        data.put("body", order.getBody());//商品详情

        if (properties.containsKey("app_id")) {
            data.put("app_id", properties.getProperty("app_id"));//客户端号
        }
        if (properties.containsKey("app_id")) {
            data.put("appenv", properties.getProperty("appenv"));//客户端来源
        }
        data.put("goods_type", properties.getProperty("goods_type", "1"));//商品类型 具体区分本地交易的商品类型。1：实物交易； 0：虚拟交易。默认为1（实物交易）。
        data.put("rn_check", properties.getProperty("rn_check", "F"));//是否发起实名校验 T：发起实名校验；F：不发起实名校验。
        data.put("it_b_pay", properties.getProperty("it_b_pay", order.getExpires() + "m"));//未付款交易的超时时间 设置未付款交易的超时时间，一旦超时，该笔交易就会自动被关闭。当用户输入支付密码、点击确认付款后（即创建支付宝交易后）开始计时。取值范围：1m～15d，或者使用绝对时间（示例格式：2014-06-13 16:00:00）。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。该参数数值不接受小数点，如1.5h，可转换为90m。
        if (properties.containsKey("extern_token")) {
            data.put("extern_token", properties.getProperty("extern_token"));//授权令牌 开放平台返回的包含账户信息的token（授权令牌，商户在一定时间内对支付宝某些服务的访问权限）。通过授权登录后获取的alipay_open_id，作为该参数的value，登录授权账户即会为支付账户。
        }
        if (properties.containsKey("out_context")) {
            data.put("out_context", properties.getProperty("out_context"));//商户业务扩展参数 业务扩展参数，支付宝特定的业务需要添加该字段，json格式。 商户接入时和支付宝协商确定。
        }

        data.put("sign_type", "RSA");//签名方式
        data.put("sign", StringUtil.encodeURI(sign(data,data.get("sign_type"), getPrivateKey(config, data.get("sign_type"))), INPUT_CHARSET));

        return SignUtil.coverMapString(data);
    }

    private static Map<String, String> parse(String result) {
        String tresult = result;
        Map<String, String> appdata = new HashMap<>();
        if (result.contains("result")) {//为手机同步支付通知
            if (RegexpUtil.isMatch(tresult, REGEXP_PARSE)) { // ios 先去掉 首尾 的括号
                tresult = RegexpUtil.parseGroup(result, REGEXP_PARSE, 1);
            }
            assert tresult != null;
            for (String _ps : tresult.split(";")) {
                String key = _ps.substring(0, _ps.indexOf('='));
                String value = _ps.substring(_ps.indexOf('=') + 1);
                if (RegexpUtil.isMatch(value, REGEXP_PARSE)) {
                    appdata.put(key, RegexpUtil.parseGroup(value, REGEXP_PARSE, 1));
                } else {
                    appdata.put(key, value);
                }
            }
        }
        return SignUtil.parseQuery(appdata.isEmpty() ? result : appdata.get("result"), appdata.isEmpty());
    }

    @Override
    public String payNotify(Payment payment, String result) throws PayException {
        PayConfig config = payment.getPayConfig();
        try {
            Map<String, String> data = parse(result);

            if (!verifyNotifyId(config.getBargainorId(), data.get("notify_id"))) {
                throw new RestException("支付宝 notify_id 验证失败");
            }
            //验证签名
            if (!verify(data, getPublicKey(config, data.get("sign_type")))) {
                throw new RestException("支付宝返回的响应签名错误");
            }
            //记录交易流水
            payment.setTradeNo(data.get("trade_no"));
            if ("WAIT_BUYER_PAY".equals(data.get("trade_status"))) {//交易创建，等待买家付款。如果有支付过期定时器的话,现在可以启动了
                // Do Nothing
                LOG.debug("WAIT_BUYER_PAY ... ");
            } else if ("TRADE_SUCCESS".equals(data.get("trade_status"))) {//交易成功，且可对该交易做操作，如：多级分润、退款等。
                if (data.containsKey("gmt_payment")) {
                    payment.setTradeTime(DateUtil.parse(data.get("gmt_payment"), "yyyy-MM-dd HH:mm:ss"));
                } else {
                    payment.setTradeTime(DateUtil.parse(data.get("notify_time"), "yyyy-MM-dd HH:mm:ss"));// 如果为同步通知,可以发起一笔查询交易来获取交易时间
                }
                payment.setStatus(PaymentStatus.success);
            } else if ("TRADE_FINISHED".equals(data.get("trade_status"))) {//交易成功且结束，即不可再做任何操作。
                payment.setStatus(PaymentStatus.finished);
            } else if ("TRADE_CLOSED".equals(data.get("trade_status"))) {//在指定时间段内未支付时关闭的交易；or 在交易完成全额退款成功时关闭的交易。
                payment.setStatus(PaymentStatus.close);
            }
            return data.containsKey("gmt_payment") ? "success" : null;
        } finally {
            this.log("in", "notify", payment, config, result);
        }
    }

    @Override
    public String refund(Refund refund) throws PayException {
        PayConfig config = refund.getPayConfig();
        Payment payment = refund.getPayment();

        final Map<String, String> data = new TreeMap<>();
        try {
            data.put("service", "refund_fastpay_by_platform_pwd");
            data.put("partner", config.getBargainorId());
            data.put("_input_charset", INPUT_CHARSET);
            data.put("sign_type", "MD5");
            data.put("notify_url", paySettings.getUrl() + "/pays/" + refund.getSn() + "/notify");
            data.put("seller_email", config.get(EXT_SELLER_EMAIL, String.class));
            data.put("seller_user_id", config.getBargainorId());
            data.put("refund_date", DateUtil.format(refund.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            data.put("batch_no", DateUtil.format("yyyyMMdd") + refund.getSn());
            data.put("batch_num", "1");
            data.put("detail_data", payment.getTradeNo() + "^" + RMB_YUAN_FORMAT.format(refund.getTotalAmount()) + "^退款");

            data.put("sign", sign(data, data.get("sign_type"),config.getBargainorKey()));


            Map<String, Object> params = new HashMap<>();
            params.put("url", urls.paymentUrl);
            params.put("params", data.entrySet());
            return HandlebarsTemplateUtils.processTemplateIntoString(HandlebarsTemplateUtils.template("/org.jfantasy.pay.product.template/pay"), params);

        } catch (IOException e) {
            LOG.error(e);
            throw new RestException("调用支付宝接口,网络错误!");
        }
    }

    @Override
    public PaymentStatus query(Payment payment) throws PayException {
        PayConfig config = payment.getPayConfig();

        final Map<String, String> data = new TreeMap<>();
        //公共请求参数
        data.put("app_id", "2088021598024164");//支付宝分配给开发者的应用Id
        data.put("method", "alipay.trade.query");
        data.put("format", "JSON");
        data.put("charset", INPUT_CHARSET);
        data.put("sign_type", "RSA");
        data.put("timestamp", DateUtil.format("yyyy-MM-dd HH:mm:ss"));
        data.put("version", "1.0");//调用的接口版本，固定为：1.0

        //请求参数
        Map<String, String> bizcontent = new TreeMap<>();
        bizcontent.put("out_trade_no", payment.getSn());//商户订单号

        data.put("biz_content", JSON.serialize(bizcontent));
        data.put("sign", sign(data,data.get("sign_type"), getPrivateKey(config, data.get("sign_type"))));

        try {
            Response response = HttpClientUtil.doPost(urls.refundUrl, data);
            return PaymentStatus.valueOf(response.text());
        } catch (IOException e) {
            LOG.error(e);
            return null;
        }
    }

    @Override
    public void close(Payment payment) throws PayException {
        // Do Nothing
    }

    @Override
    public String payNotify(Refund refund, String result) throws PayException {
        PayConfig payConfig = refund.getPayConfig();

        Map<String, String> data = SignUtil.parseQuery(result, true);

        LOG.debug(data);

        if ("batch_refund_notify".equals(data.get("notify_type"))) {
            if (!verifyNotifyId(payConfig.getBargainorId(), data.get("notify_id"))) {
                throw new RestException("支付宝 notify_id 验证失败");
            }
            //验证签名
            if (!verify(data, getPublicKey(payConfig, data.get("sign_type")))) {
                throw new RestException("支付宝返回的响应签名错误");
            }
            if ("1".equals(data.get("success_num"))) {
                refund.setStatus(RefundStatus.success);
                refund.setTradeTime(DateUtil.parse(data.get("notify_time"), "yyyy-MM-dd HH:mm:ss"));
                return "success";
            }
        }
        return null;
    }

    private static class BankCode {
        private String code;
        private String name;

        BankCode(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }
    }

    private static class Urls {
        /**
         * 支付接口
         */
        String paymentUrl;
        /**
         * 退款接口
         */
        String refundUrl;
        /**
         * 查询地址
         */
        String queryUrl;
    }

    /**
     * 获取远程服务器ATN结果,验证返回URL
     *
     * @param partner  合作身份者ID
     * @param notifyId 通知校验ID
     * @return 服务器ATN结果
     * 验证结果集：
     * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空
     * true 返回正确信息
     * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private static boolean verifyNotifyId(String partner, String notifyId) {
        //获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
        String veryfyUrl = HTTPS_VERIFY_URL + "partner=" + partner + "&notify_id=" + notifyId;
        try {
            URL url = new URL(veryfyUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            return "true".equalsIgnoreCase(in.readLine());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public static String getPrivateKey(PayConfig config, String type) throws PayException {
        switch (type) {
            case "MD5":
                return config.getBargainorKey();
            case "RSA":
                return config.get("rsaPrivateKey", String.class).replaceAll("[-]+[A-Z ]+[-]+", "").replaceAll("[\\n]", "");
            default:
                throw new PayException("获取 PrivateKey 出错,不支持的加密方式:" + type);
        }
    }

    private String getPublicKey(PayConfig config, String type) throws PayException {
        switch (type) {
            case "MD5":
                return config.getBargainorKey();
            case "RSA":
                return config.get("rsaPublicKey", String.class).replaceAll("[-]+[A-Z ]+[-]+", "").replaceAll("[\\n]", "");
            default:
                throw new PayException("获取 PublicKey 出错,不支持的加密方式:" + type);
        }
    }

    /**
     * 生成签名结果
     *
     * @param data 要签名的数组
     * @param key  sign_type = MD5 时为 安全校验码 如果为 sign_type = RSA 时为
     * @return 签名结果字符串
     */
    public static String sign(Map<String, String> data,String signType, String key) throws PayException {
//        if (!data.containsKey("sign_type")) {
//            data.put("sign_type", "MD5");
//        }
//        String signType = data.get("sign_type");
        if ("MD5".equals(signType)) {
            return MD5.sign(SignUtil.coverMapString(data, "sign", "sign_type"), key, INPUT_CHARSET);
        } else if ("RSA".equals(signType)) {
            return RSA.sign(SignUtil.coverMapString(data, "sign", "sign_type"), key, INPUT_CHARSET);
        } else if ("DSA".equals(signType)) {
            throw new PayException("DSA 签名方式还未实现");
        }
        throw new PayException("不支持的签名方式 => " + signType);
    }

    public static boolean verify(Map<String, String> data, String key) throws PayException {
        String signType = data.get("sign_type");
        if ("MD5".equals(signType)) {
            return MD5.verify(SignUtil.coverMapString(data, "sign", "sign_type"), data.get("sign"), key, INPUT_CHARSET);
        } else if ("RSA".equals(signType)) {
            return RSA.verify(SignUtil.coverMapString(data, "sign", "sign_type"), data.get("sign"), key, INPUT_CHARSET);
        } else if ("DSA".equals(signType)) {
            throw new PayException("DSA 签名方式还未实现");
        }
        throw new PayException("不支持的签名方式 => " + signType);
    }

}