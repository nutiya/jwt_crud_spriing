package com.example.StudentManagement.websocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send a global WebSocket event.
     * @param eventType any string describing the event (e.g., "STUDENT_CREATED", "ROLE_UPDATED")
     * @param payload any object you want to send
     */
    public void sendEvent(String eventType, Object payload) {
        try {
            messagingTemplate.convertAndSend("/topic/events", new WebSocketEvent<>(eventType, payload));
        } catch (Exception e) {
            log.error("Failed to send WebSocket event: {}", eventType, e);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class WebSocketEvent<T> {
        private final String eventType;
        private final T payload;
        private final long timestamp = System.currentTimeMillis();
    }
}