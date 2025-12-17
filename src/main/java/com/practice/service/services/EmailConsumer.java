/*
 * FuckUB Pty. Ltd. ("LKG") CONFIDENTIAL
 * Copyright (c) 2025 FuckUB project Pty. Ltd. All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of LKG. The intellectual and technical concepts contained
 * herein are proprietary to LKG and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from LKG.  Access to the source code contained herein is hereby forbidden to anyone except current LKG employees, managers or contractors who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 */
package com.practice.service.services;

import com.practice.service.dto.EmailEvent;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.thymeleaf.context.Context;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailConsumer {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailConsumer(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @RabbitListener(queues = "${rabbitmq.email.queue}")
    public void receive(EmailEvent event) {
        Context context = new Context();
        context.setVariables(event.getVariables());

        String html = templateEngine.process(
                event.getTemplate(),
                context
        );

        sendHtmlEmail(event.getTo(), event.getSubject(), html);
    }

    public void sendHtmlEmail(String to, String subject, String html) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
