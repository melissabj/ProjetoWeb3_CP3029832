package com.msa.emailservice.dtos;

public record VerifyCodeDto(
        String email,
        String code
) {}
