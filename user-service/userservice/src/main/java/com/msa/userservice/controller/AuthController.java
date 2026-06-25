package com.msa.userservice.controller;

import com.msa.userservice.dtos.EmailDto;
import com.msa.userservice.dtos.RecoveryJwtTokenDto;
import com.msa.userservice.dtos.VerifyCodeRequest;
import com.msa.userservice.entities.User;
import com.msa.userservice.services.CodigoCacheService;
import com.msa.userservice.services.JwtTokenService;
import com.msa.userservice.services.UserService;
import com.msa.userservice.services.UserDetailsImpl;
import com.msa.userservice.producers.UserProducer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private CodigoCacheService codigoCacheService;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserProducer producer;

    @PostMapping("/request-code")
    public ResponseEntity<Void> requestCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        // Centralizado: Delega a criação/busca do usuário totalmente para o UserService
        User user = userService.getOrCreateUserForCode(email);

        // Gera o código OTP de 6 dígitos
        String codigoOtp = String.format("%06d", new Random().nextInt(999999));
        codigoCacheService.salvarCodigo(email, codigoOtp);

        // Instancia o Record DTO correto e envia para a mensageria
        EmailDto emailDto = new EmailDto(
                user.getId(),
                email,
                "Seu código de acesso",
                "Seu código é: " + codigoOtp
        );
        producer.sendEmail(emailDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeRequest dto) {
        String codigoSalvo = codigoCacheService.obterCodigoValido(dto.email());

        if (codigoSalvo != null && codigoSalvo.equals(dto.code())) {
            codigoCacheService.removerCodigo(dto.email());

            // Busca a entidade de forma limpa pelo UserService
            User user = userService.findEntityByEmail(dto.email());

            String token = jwtTokenService.generateToken(new UserDetailsImpl(user));
            return ResponseEntity.ok(new RecoveryJwtTokenDto(token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Código inválido ou expirado."));
    }
}