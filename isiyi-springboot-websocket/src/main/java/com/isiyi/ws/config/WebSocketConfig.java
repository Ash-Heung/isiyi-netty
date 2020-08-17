package com.isiyi.ws.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注册一个websocket端点，客户端将使用它连接到我们的websocket服务器。
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //withSockJS()是用来为不支持websocket的浏览器启用后备选项，使用了SockJS。
        registry.addEndpoint("/ws").withSockJS();
    }

    /**
     * 一个消息代理，用于将消息从一个客户端路由到另一个客户端
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //以“/app”开头的消息应该路由到消息处理方法
        registry.setApplicationDestinationPrefixes("/app");
        //以“/topic”开头的消息应该路由到消息代理。消息代理向订阅特定主题的所有连接客户端广播消息。
        registry.enableSimpleBroker("/topic");
    }


}
