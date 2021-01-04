package com.zh.study.studyrabbitmq.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.ExchangeTypes;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ Exchange交换器类型 direct ：
 *    将消息发送给指定routingKey的队列
 * @date 2021/1/4
 */
public class DirectProductTest {
    private static final String EXCHANGE_NAME = "study.exchange.direct";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, ExchangeTypes.DIRECT);
            for (int i = 0; i < 10; i++) {
                String msg = "direct: send msg " +i;
                channel.basicPublish(EXCHANGE_NAME, "study.direct.queue", null, msg.getBytes("UTF-8"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
