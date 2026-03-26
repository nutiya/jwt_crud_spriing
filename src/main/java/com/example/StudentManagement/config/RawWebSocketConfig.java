package com.example.StudentManagement.config;

import com.example.StudentManagement.websocket.RawWebSocketController;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket   // ✅ IMPORTANT (NOT MessageBroker)
@AllArgsConstructor
public class RawWebSocketConfig implements WebSocketConfigurer {

    private final RawWebSocketController rawWebSocketController;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(rawWebSocketController, "/ws-raw")
                .setAllowedOriginPatterns("*"); // allow Postman
    }
}