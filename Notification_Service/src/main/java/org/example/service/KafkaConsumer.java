package org.example.service;

import org.example.dto.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private final EmailService emailService;

    public KafkaConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(UserEvent event) {
        if (event == null || event.getEmail() == null || event.getOperation() == null) {
            log.error("Received invalid user event: {}", event);
            return;
        }

        log.info("Received user event for email: {}, operation: {}", event.getEmail(), event.getOperation());

        try {
            switch (event.getOperation()) {
                case UserEvent.CREATE:
                    emailService.sendEmail(
                            event.getEmail(),
                            "Аккаунт создан",
                            "Здравствуйте! Ваш аккаунт был успешно создан."
                    );
                    break;
                case UserEvent.DELETE:
                    emailService.sendEmail(
                            event.getEmail(),
                            "Аккаунт удалён",
                            "Здравствуйте! Ваш аккаунт был удалён."
                    );
                    break;
                default:
                    log.warn("Unknown operation: {}", event.getOperation());
            }
        } catch (Exception e) {
            log.error("Failed to process user event for email: {}", event.getEmail(), e);
        }
    }
}