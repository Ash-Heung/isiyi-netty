package com.isiyi.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * //使用Configuration注解标识这是一个Springboot的配置类.
 * //使用此注解来标识使能WebSocket的broker.即使用broker来处理消息.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 用来注册Endpoint，“/gs-guide-websocket”即为客户端尝试建立连接的地址。
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket").withSockJS();
    }

    /**
     * 实现WebSocketMessageBrokerConfigurer中的此方法，配置消息代理（broker）
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //启用SimpleBroker，使得订阅到此"topic"前缀的客户端可以收到greeting消息.
        registry.enableSimpleBroker("/topic");
        //将"app"前缀绑定到MessageMapping注解指定的方法上。如"app/hello"被指定用greeting()方法来处理.
        registry.setApplicationDestinationPrefixes("/app");
    }
}
