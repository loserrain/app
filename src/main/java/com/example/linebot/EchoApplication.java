package com.example.linebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.linecorp.bot.messaging.client.MessagingApiClient;
import com.linecorp.bot.messaging.model.ReplyMessageRequest;
import com.linecorp.bot.messaging.model.TextMessage;
import com.linecorp.bot.spring.boot.handler.annotation.EventMapping;
import com.linecorp.bot.spring.boot.handler.annotation.LineMessageHandler;
import com.linecorp.bot.webhook.model.Event;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.TextMessageContent;

@SpringBootApplication
@LineMessageHandler
public class EchoApplication {
    private final Logger log = LoggerFactory.getLogger(EchoApplication.class);
    private final MessagingApiClient messagingApiClient;

    public static void main(String[] args) {
        SpringApplication.run(EchoApplication.class, args);
    }

    public EchoApplication(MessagingApiClient messagingApiClient) {
        this.messagingApiClient = messagingApiClient;
    }

    @EventMapping
    public void handleTextMessageEvent(MessageEvent event) {
        log.info("event: " + event);
        if (event.message() instanceof TextMessageContent) {
            TextMessageContent message = (TextMessageContent) event.message();
            final String originalMessageText = message.text();
            messagingApiClient.replyMessage(new ReplyMessageRequest(
                    event.replyToken(),
                    List.of(new TextMessage(originalMessageText)),
                    false));
        }
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }


    @EventMapping
    public void handleTextMessageTestEvent(MessageEvent event) {

        if (event.message() instanceof TextMessageContent) {
            TextMessageContent message = (TextMessageContent) event.message();
            String messageText = message.text();

            // 檢查訊息是否包含 "a"
            if (messageText.contains("a")) {
                messageText = "來AAAAAA";
            } else {
                messageText = "回應測試";
            }

            // 創建一個新的文字訊息
            TextMessage textMessage = new TextMessage(messageText);

            // 創建一個新的回傳訊息請求
            ReplyMessageRequest replyMessageRequest = new ReplyMessageRequest(
                    event.replyToken(),
                    List.of(textMessage),
                    false);

            messagingApiClient.replyMessage(replyMessageRequest);
        }
    }
}