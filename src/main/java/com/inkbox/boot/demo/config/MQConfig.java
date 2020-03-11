package com.inkbox.boot.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息中间件相关配置项
 */
@Configuration
public class MQConfig {

    public static final String DELAY_QUEUEA_ROUTING_KEY = "DELAY_QUEUEA_ROUTING_KEY";
    public Logger logger = LoggerFactory.getLogger(getClass());

    public static final String WORK_EXCHANGE = "WORK_EXCHANGE";
    public static final String DEAD_LETTER_EXCHANGE = "DEAD_LETTER_EXCHANGE";
    public static final String DEAD_LETTER_QUEUE_ROUTING_KEY = "DEAD_LETTER_QUEUE_ROUTING_KEY";

    /**
     * 业务交换机，也是本次中的延时队列交换机
     *
     * @return
     */
    @Bean("workExchange")
    public DirectExchange directExchange() {
        return new DirectExchange(WORK_EXCHANGE);
    }

    /**
     * 死信交换机
     */
    @Bean("deadExchange")
    public DirectExchange deadExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    /**
     * 业务队列，该队列实际上只是一个消息的存储空间，消息不应该被消费
     *
     * @return
     */
    @Bean
    public Queue workQueue() {
        return QueueBuilder.durable("WORK_QUEUE")
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)//x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_QUEUE_ROUTING_KEY)//x-dead-letter-routing-key  这里声明当前队列的死信路由key
                .withArgument("x-message-ttl", 3000)//声明队列的TTL，单位毫秒
                .build();
    }

    /**
     * 死信队列
     *
     * @return
     */
    @Bean
    public Queue deadQueue() {
        return QueueBuilder.durable("deadQueue").build();
    }

    @Bean
    public ConnectionFactory rabbitConnectionFactoryBean() {

        CachingConnectionFactory bean = new CachingConnectionFactory();

        bean.setHost("192.168.1.5");
        bean.setPort(5672);
        bean.setVirtualHost("host");
        bean.setPassword("admin");
        bean.setUsername("admin");

        return bean;
    }

    /**
     * 业务队列和交换机的绑定关系
     *
     * @return
     */
    @Bean
    public Binding workBinding(@Qualifier("workQueue") Queue workQueue,
                               @Qualifier("workExchange") DirectExchange exchange) {

        return BindingBuilder.bind(workQueue).to(exchange).with(DELAY_QUEUEA_ROUTING_KEY);
    }

    // 声明死信队列A绑定关系
    @Bean
    public Binding deadLetterBindingA(@Qualifier("deadQueue") Queue queue,
                                      @Qualifier("deadExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DEAD_LETTER_QUEUE_ROUTING_KEY);
    }
}
