package com.msa.userservice.controller;

import com.msa.userservice.dtos.CreateUserDto;
import com.msa.userservice.dtos.LoginUserDto;
import com.msa.userservice.dtos.RecoveryJwtTokenDto;
import com.msa.userservice.dtos.UpdateProfileDto;
import com.msa.userservice.dtos.UserProfileDto;
import com.msa.userservice.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(@Valid @RequestBody LoginUserDto loginUserDto) {
        return ResponseEntity.ok(userService.authenticateUser(loginUserDto));
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        userService.createUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Endpoint Etapa 4: Atualização de perfil com proteção BOLA
    @PostMapping("/update-profile")
    public ResponseEntity<UserProfileDto> updateProfile(Authentication authentication, @RequestBody UpdateProfileDto dto) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.updateProfile(email, dto));
    }

    // Endpoint Etapa 4: Obter dados do próprio perfil
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMe(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserInformation(authentication));
    }

    @GetMapping("/test/customer")
    public ResponseEntity<String> testCustomer() {
        return ResponseEntity.ok("Acesso Autorizado: Você possui permissão para acessar este recurso!");
    }
}