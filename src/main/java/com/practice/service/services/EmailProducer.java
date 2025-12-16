package com.practice.service.services;

import com.practice.service.dto.EmailEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailProducer {
    private final RabbitTemplate rabbitTemplate;

    public EmailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmail(EmailEvent email) {
        rabbitTemplate.convertAndSend(
                "email.exchange",
                "email.send",
                email
        );
    }
}
