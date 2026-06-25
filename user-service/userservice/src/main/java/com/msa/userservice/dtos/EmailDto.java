package com.msa.userservice.dtos;

import java.util.UUID;

public record EmailDto(
        UUID userId,
        String emailTo,
        String subject,
        String text
) {}