package com.zh.study.studyrabbitmq.work;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ work queue工作模式
 * 一个生产者对应多个消费者，多个消费者之间是竞争关系，即一条消息只能被一个消费者消费
 * @date 2021/1/4
 */
public class WorkProductTest {
    private final static String QUEUE_NAME = "queue.work";

    public static void main(String[] args) throws IOException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
            Connection connection = null;
            try {
                connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel();
                //autoDelete = true 表示消息被消费后自动删除
                channel.queueDeclare(QUEUE_NAME, false, false, true, null);

                String msg = String.join(".", "First msg", "Second msg", "Third msg");
                channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
    }
}
