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
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@LineMessageHandler
public class OrderController {

//
//    @Qualifier("openaiRestTemplate")
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Value("${openai.model}")
//    private String model;
//
//    @Value("${openai.api.url}")
//    private String apiUrl;


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
            String messageText = message.text();

            // 創建一個新的 RestTemplate 物件
            RestTemplate restTemplate = new RestTemplate();

// Set the read timeout to 5000 milliseconds (30 seconds)
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(30000);

// Set the connect timeout to 5000 milliseconds (5 seconds)
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(30000);

            // 呼叫 API 並獲取回應
            String url = "http://140.130.33.150:8081/chat?prompt=" + messageText;
            String response = restTemplate.getForObject(url, String.class);

            // 檢查回應是否有效，並將回應訊息設定為要發送回 Line Bot 的訊息
            if (response != null && !response.isEmpty()) {
                messageText = response;
            } else {
                messageText = "No response";
            }

            // 創建一個新的文字訊息
            TextMessage textMessage = new TextMessage(messageText);

            // 創建一個新的回傳訊息請求
            ReplyMessageRequest replyMessageRequest = new ReplyMessageRequest(
                    event.replyToken(),
                    List.of(textMessage),
                    false);

            // 發送回應訊息到 Line Bot
            messagingApiClient.replyMessage(replyMessageRequest);
        }
    }

//    @EventMapping
//    public void handleTextMessageTestEvent(MessageEvent event) {
//        if (event.message() instanceof TextMessageContent) {
//            TextMessageContent message = (TextMessageContent) event.message();
//            String messageText = message.text();
//
//            // 創建一個新的 ChatRequest 物件，並將 Line Bot 的訊息作為提示
//            ChatRequest request = new ChatRequest(model, messageText);
//
//            // 呼叫 OpenAI API 並獲取回應
//            ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);
//
//            // 檢查回應是否有效，並將回應訊息設定為要發送回 Line Bot 的訊息
//            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
//                messageText = response.getChoices().get(0).getMessage().getContent();
//            } else {
//                messageText = "No response";
//            }
//
//            // 創建一個新的文字訊息
//            TextMessage textMessage = new TextMessage(messageText);
//
//            // 創建一個新的回傳訊息請求
//            ReplyMessageRequest replyMessageRequest = new ReplyMessageRequest(
//                    event.replyToken(),
//                    List.of(textMessage),
//                    false);
//
//            // 發送回應訊息到 Line Bot
//            messagingApiClient.replyMessage(replyMessageRequest);
//        }
//    }



}
