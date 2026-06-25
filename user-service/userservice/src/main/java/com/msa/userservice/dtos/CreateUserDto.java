package com.msa.userservice.dtos;

import com.msa.userservice.enums.RoleName;

public record CreateUserDto(
        String email,
        String password,
        RoleName role
) {}
