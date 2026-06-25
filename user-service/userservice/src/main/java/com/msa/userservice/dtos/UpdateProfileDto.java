package com.msa.userservice.dtos;

import com.msa.userservice.enums.RoleName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProfileDto(
        @NotBlank String name,
        @NotNull RoleName role
) {}