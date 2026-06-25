package com.msa.emailservice.models;

import com.msa.emailservice.enums.StatusEmail;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID emailId;

    private UUID userId;

    private String emailFrom;

    private String emailTo;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String text;

    private LocalDateTime sendDateEmail;

    @Enumerated(EnumType.STRING)
    private StatusEmail status;
}