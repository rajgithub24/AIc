package com.aicontract.contractanalyzer.service;

import com.aicontract.contractanalyzer.dto.AuthRequestDto;
import com.aicontract.contractanalyzer.dto.AuthResponseDto;
import com.aicontract.contractanalyzer.entity.User;
import com.aicontract.contractanalyzer.repository.UserRepository;
import com.aicontract.contractanalyzer.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthResponseDto register(
            AuthRequestDto request
    ) {

        User user = User.builder()
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role("USER")
                .build();

        userRepository.save(user);

        String token =
                jwtService.generateToken(
                        user.getEmail()
                );

        return AuthResponseDto.builder()
                .token(token)
                .build();
    }

    public AuthResponseDto login(
            AuthRequestDto request
    ) {

        User user = userRepository.findByEmail(
                request.getEmail()
        ).orElseThrow(() ->
                new RuntimeException("Invalid credentials")
        );

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException(
                    "Invalid credentials"
            );
        }

        String token =
                jwtService.generateToken(
                        user.getEmail()
                );

        return AuthResponseDto.builder()
                .token(token)
                .build();
    }
}