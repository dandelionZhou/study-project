package com.zh.study.studyrabbitmq.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.ExchangeTypes;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ 发布/订阅模式：RabbitMQ将消息发送到交换器，根据交换器不同类型的不同路由规则，将消息路由到消息队列
 *  RabbitMQ中Exchange交换器类型：
 *  direct：通过routingKey绑定(即队列名)
 *  fanout：发送消息到所有绑定的队列,一条消息能被多个消费者消费
 *  topic：模式匹配，通过指定模式，将消息发到匹配模式的所有队列(需要将队列绑定到模式上)
 *  headers：和direct基本一样，但性能差，少用。。。
 *
 * @date 2021/1/4
 */
public class PubProductTest {
    private final static String EXCHANGE_NAME = "study.exchange";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, ExchangeTypes.FANOUT);
            for (int i = 0; i < 100; i++) {
                String msg = "hello word." + i;
                channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes("UTF-8"));
                System.out.println("[x] send msg:" + msg);
                TimeUnit.SECONDS.sleep(1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
