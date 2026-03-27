package com.example.StudentManagement.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Controller
@RequiredArgsConstructor
public class RawWebSocketController extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    // ✅ ObjectMapper that can handle LocalDateTime
    private final ObjectMapper objectMapper;

    public RawWebSocketController() {
        this.objectMapper = new ObjectMapper();
        // Register module to support Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule());
        // Serialize dates as ISO strings instead of timestamps
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
    }

    public void sendEvent(String eventType, Object payload) {
        try {
            System.out.println("🔥 Sending event: " + eventType);
            System.out.println("👥 Active sessions: " + sessions.size());

            String json = objectMapper.writeValueAsString(
                    new Message(eventType, payload)
            );

            sessions.forEach(session -> {
                try {
                    session.sendMessage(new TextMessage(json));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ clean JSON structure
    private static class Message {
        public String eventType;
        public Object payload;

        public Message(String eventType, Object payload) {
            this.eventType = eventType;
            this.payload = payload;
        }
    }
}