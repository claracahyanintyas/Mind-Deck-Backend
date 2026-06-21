package org.individualproject.flashcards.infrastructure.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Channels starting with /topic are broadcast brokers (Pub/Sub)
        config.enableSimpleBroker("/topic");
        // Messages starting with /app are routed to your @MessageMapping controllers
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The endpoint the frontend uses to connect
        registry.addEndpoint("/ws-classroom")
                .setAllowedOriginPatterns("http://145.220.72.104", "http://localhost:5173") // Configure according to your CORS setup
                .withSockJS(); // Fallback for browsers that don't support native WebSockets
    }
}
