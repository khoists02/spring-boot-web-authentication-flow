package com.example.demo.services;

import com.example.demo.dto.EmailEvent;
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
