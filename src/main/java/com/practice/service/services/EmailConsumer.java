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
