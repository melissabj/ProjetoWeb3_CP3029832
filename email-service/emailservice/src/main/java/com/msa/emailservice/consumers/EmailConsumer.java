package com.msa.emailservice.consumers;

import com.msa.emailservice.dtos.EmailRecordDto;
import com.msa.emailservice.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(
            queues = "${broker.queue.email.name}"
    )
    public void listenEmailQueue(
            EmailRecordDto dto
    ) {
        emailService.sendEmail(dto);
    }
}