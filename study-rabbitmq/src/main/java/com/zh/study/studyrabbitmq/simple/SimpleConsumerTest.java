package com.zh.study.studyrabbitmq.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ 简单模式：消费者消费消息
 * @date 2021/1/4
 */
public class SimpleConsumerTest {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        //获取连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try {
            connectionFactory.setHost("localhost");
            //获取连接
            Connection connection = connectionFactory.newConnection();
            //获取信道
            Channel channel = connection.createChannel();
            //指定队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //获取消息
            DeliverCallback deliverCallback = ((consumerTag, message) -> {
               String msg = new String(message.getBody(), "UTF-8");
                System.out.println("received msg :" + msg);
            });
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
