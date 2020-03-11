package com.inkbox.boot.demo.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.impl.recovery.QueueRecoveryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * 死信队列消费者
 */
@Component
public class DeadQueueListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 死信消费队列
     *
     * @param message 消息
     * @param channel 通道
     * @throws IOException
     */
    @RabbitListener(queues = "deadQueue")
    public void receiveA(Message message, Channel channel) throws IOException {
        String msg = new String(message.getBody());
        logger.debug("当前时间：{},死信队列收到消息：{},headers={}", new Date().toString(), msg, message.getMessageProperties().getHeaders());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

//    @RabbitListener(queues = "WORK_QUEUE")
//    public void receiveB(Message message, Channel channel) throws IOException {
//        String msg = new String(message.getBody());
//        logger.info("当前时间：{},业务队列收到消息：{} headers={}", new Date().toString(), msg, message.getMessageProperties().getHeaders());
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//    }

    /**
     * 真正的延时队列
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = "CUSTOM_DELAYED_QUEUE_NAME")
    public void delay(Message message, Channel channel) throws IOException {
        String msg = new String(message.getBody());
        logger.info("当前时间：{},真正的延时队列收到消息：{} headers={}", new Date().toString(), msg, message.getMessageProperties().getHeaders());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
