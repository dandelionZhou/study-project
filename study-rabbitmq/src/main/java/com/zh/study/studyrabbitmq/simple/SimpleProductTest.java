package com.zh.study.studyrabbitmq.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ 简单模式：生产者发布消息
 * @date 2021/1/4
 */
public class SimpleProductTest {
    //队列名称
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        //获取连接创建工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try{
            //配置好rabbitmq环境后创建TCP连接
            Connection connection = connectionFactory.newConnection();
            //创建信道，多路复用一次连接
            Channel channel = connection.createChannel();
            //声明队列，包括队列名称，是否独占，是否自动删除
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //将消息发布到消息队列
            channel.basicPublish("", QUEUE_NAME, null, "hello rabbitmq".getBytes());
            System.out.println("Sent msg...");
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
