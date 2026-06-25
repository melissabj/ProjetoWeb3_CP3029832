package com.msa.emailservice.services;

import com.msa.emailservice.dtos.EmailRecordDto;
import com.msa.emailservice.enums.StatusEmail;
import com.msa.emailservice.models.EmailModel;
import com.msa.emailservice.repositories.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    public void sendEmail(EmailRecordDto dto) {

        EmailModel emailModel = new EmailModel();

        emailModel.setUserId(dto.userId());
        emailModel.setEmailTo(dto.emailTo());
        emailModel.setSubject(dto.subject());
        emailModel.setText(dto.text());
        emailModel.setEmailFrom(emailFrom);
        emailModel.setSendDateEmail(LocalDateTime.now());

        try {

            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setFrom(emailFrom);
            message.setTo(dto.emailTo());
            message.setSubject(dto.subject());
            message.setText(dto.text());

            emailSender.send(message);

            emailModel.setStatus(StatusEmail.SENT);

        } catch (Exception ex) {

            emailModel.setStatus(StatusEmail.ERROR);
        }

        emailRepository.save(emailModel);
    }
}