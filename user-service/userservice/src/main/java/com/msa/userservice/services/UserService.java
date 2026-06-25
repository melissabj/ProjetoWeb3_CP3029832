package com.msa.userservice.services;

import com.msa.userservice.dtos.CreateUserDto;
import com.msa.userservice.dtos.LoginUserDto;
import com.msa.userservice.dtos.RecoveryJwtTokenDto;
import com.msa.userservice.dtos.UpdateProfileDto;
import com.msa.userservice.dtos.UserProfileDto;
import com.msa.userservice.entities.Role;
import com.msa.userservice.entities.User;
import com.msa.userservice.enums.RoleName;
import com.msa.userservice.repositories.RoleRepository;
import com.msa.userservice.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginDto) {
        var authToken = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());
        var authentication = authenticationManager.authenticate(authToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtTokenService.generateToken(userDetails);
        return new RecoveryJwtTokenDto(token);
    }

    @Transactional
    public void createUser(CreateUserDto createDto) {
        Role role = roleRepository.findByName(createDto.role())
                .stream()
                .findFirst()
                .orElseGet(() -> roleRepository.save(Role.builder().name(createDto.role()).build()));

        User newUser = User.builder()
                .email(createDto.email())
                .password(passwordEncoder.encode(createDto.password()))
                .roles(new ArrayList<>(List.of(role)))
                .build();
        userRepository.save(newUser);
    }

    public User findEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public UserProfileDto getUserInformation(Authentication authentication) {
        User user = findEntityByEmail(authentication.getName());
        return toUserProfileDto(user);
    }

    @Transactional
    public UserProfileDto updateProfile(String email, UpdateProfileDto dto) {
        User user = findEntityByEmail(email);

        Role role = roleRepository.findByName(dto.role())
                .stream()
                .findFirst()
                .orElseGet(() -> roleRepository.save(Role.builder().name(dto.role()).build()));

        user.setName(dto.name());
        user.setRoles(new ArrayList<>(List.of(role)));

        userRepository.save(user);

        return toUserProfileDto(user);
    }

    @Transactional
    public User getOrCreateUserForCode(String email) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            var randomPassword = UUID.randomUUID().toString();

            Role defaultRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                    .stream()
                    .findFirst()
                    .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_CUSTOMER).build()));

            var newUser = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(randomPassword))
                    .roles(new ArrayList<>(List.of(defaultRole)))
                    .build();

            return userRepository.save(newUser);
        });
    }

    private UserProfileDto toUserProfileDto(User user) {
        var roles = user.getRoles().stream()
                .filter(role -> role.getName() != null)
                .map(role -> role.getName().name())
                .toList();

        return new UserProfileDto(user.getId(), user.getEmail(), user.getName(), roles);
    }
}