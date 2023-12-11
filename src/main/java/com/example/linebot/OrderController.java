package com.example.linebot;


import com.example.linebot.dto.ChatRequest;
import com.example.linebot.dto.ChatResponse;
import com.linecorp.bot.messaging.client.MessagingApiClient;
import com.linecorp.bot.messaging.model.ReplyMessageRequest;
import com.linecorp.bot.messaging.model.TextMessage;
import com.linecorp.bot.spring.boot.handler.annotation.EventMapping;
import com.linecorp.bot.spring.boot.handler.annotation.LineMessageHandler;
import com.linecorp.bot.webhook.model.Event;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.TextMessageContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@LineMessageHandler
public class OrderController {


    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;


    private final Logger log = LoggerFactory.getLogger(EchoApplication.class);
    private final MessagingApiClient messagingApiClient;


    public OrderController(MessagingApiClient messagingApiClient) {
        this.messagingApiClient = messagingApiClient;
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }


    @EventMapping
    public void handleTextMessageTestEvent(MessageEvent event) {

        if (event.message() instanceof TextMessageContent) {
            TextMessageContent message = (TextMessageContent) event.message();
            String messageText ="";


            String content =  message.text();
            ChatRequest request = new ChatRequest(model,content);
            log.info("{}", content);
            ChatResponse response = restTemplate.postForObject(
                    apiUrl,
                    request,
                    ChatResponse.class);

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                messageText =  "No response";
            }else {
                messageText =  response.getChoices().get(0).getMessage().getContent();
            }

            // 創建一個新的文字訊息
            TextMessage textMessage = new TextMessage( messageText);

            // 創建一個新的回傳訊息請求
            ReplyMessageRequest replyMessageRequest = new ReplyMessageRequest(
                    event.replyToken(),
                    List.of(textMessage),
                    false);

            messagingApiClient.replyMessage(replyMessageRequest);
        }
    }



}
