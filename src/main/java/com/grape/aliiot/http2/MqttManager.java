package com.grape.aliiot.http2;

import com.aliyun.openservices.iot.api.message.api.MessageClient;
import com.aliyun.openservices.iot.api.message.callback.MessageCallback;
import com.aliyun.openservices.iot.api.message.entity.Message;
import com.grape.aliiot.message.MessageProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.Charset;

/**
 * Created with IDEA
 * description:
 * @author :YanHongBin
 * @date :Created in 2019/5/7 14:28
 */
@Component
@Scope("singleton")
public class MqttManager extends Thread{

    private static final Logger log = LoggerFactory.getLogger(MqttManager.class);

    @Resource(type = MessageProcess.class)
    private MessageProcess messageProcess;

    @Resource(type = H2ClientFactory.class)
    private H2ClientFactory h2ClientFactory;


    @Override
    public void run() {
        MessageClient messageClient = h2ClientFactory.getMessageClient();
        if (messageClient.isConnected()){
            // 如果已连接,不需要再次开启连接
            return;
        }
        /*
         * 改用lambda表达式创建连接
         */
        messageClient.connect(messageToken -> {
            Message m = messageToken.getMessage();
            System.out.println("receive message " + m);
            processMessage(m);
            return MessageCallback.Action.CommitSuccess;
        });
    }

    public void processMessage(Message m) {
        String messageId = m.getMessageId();
        String topic = m.getTopic();
        String payload = new String(m.getPayload(), Charset.forName("UTF-8"));
        log.info("messageId:{}",messageId);
        log.info("topic:{}",topic);
        log.info("payload:{}",payload);

        messageProcess.processMessage(topic,payload);
    }

    /**
     * 销毁Bean时调用
     */
    public void destroyBean() {
        System.out.println("==================================================close==================================================");
        MessageClient client = h2ClientFactory.getMessageClient();
        if (client.isConnected()){
            // 已连接,关闭连接
            System.out.println("已连接,关闭连接");
            client.disconnect();
        }
    }

}