package com.example.notification_service;

import org.example.dto.UserEvent;
import org.example.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"user-events"})
@DirtiesContext
class NotificationServiceIntegrationTest {

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @MockBean
    private EmailService emailService;

    @Test
    void shouldProcessUserCreatedEvent() {
        UserEvent event = new UserEvent();
        event.setEmail("test@example.com");
        event.setOperation(UserEvent.CREATE);

        kafkaTemplate.send("user-events", event);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(emailService).sendEmail(
                        eq("test@example.com"),
                        eq("Аккаунт создан"),
                        eq("Здравствуйте! Ваш аккаунт был успешно создан.")
                ));
    }
    @Test
    void shouldProcessUserDeletedEvent() {
        UserEvent event = new UserEvent();
        event.setEmail("test@example.com");
        event.setOperation(UserEvent.DELETE);

        kafkaTemplate.send("user-events", event);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(emailService).sendEmail(
                        eq("test@example.com"),
                        eq("Аккаунт удалён"),
                        eq("Здравствуйте! Ваш аккаунт был удалён.")
                ));
    }
}