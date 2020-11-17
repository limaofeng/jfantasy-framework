package org.jfantasy.autoconfigure;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author ChenWenJie
 * @Data 2020/11/10 11:02 上午
 **/
@Configuration
public class VersionRabbitAutoConfiguration{
    /**
     * 交换机名称
     */
    public static final String DIRECT_EXCHANGE_NAME = "vcsExchange";
    /**
     * 绑定key，交换机绑定队列时需要指定
     */
    public static final String BINGDING_KEY_VCS = "vcsKey";
    /**
     * 队列名称
     */
    public static final String VCS = "vcs";

    /**
     * 构建DirectExchange交换机
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE_NAME, true, false);
    }
    /**
     * 构建序列
     *
     * @return
     */
    @Bean
    public Queue flowQueue() {
        // 支持持久化
        return new Queue(VCS, true,false,false);
    }
    /**
     * 绑定交交换机和
     *
     * @return
     */
    @Bean
    public Binding flowBinding() {
        return BindingBuilder.bind(flowQueue()).to(directExchange()).with(BINGDING_KEY_VCS);
    }
}
