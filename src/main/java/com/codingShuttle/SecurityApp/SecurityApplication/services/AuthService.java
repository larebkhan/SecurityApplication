package com.codingShuttle.SecurityApp.SecurityApplication.services;

import com.codingShuttle.SecurityApp.SecurityApplication.dto.LoginDto;
import com.codingShuttle.SecurityApp.SecurityApplication.dto.LoginResponseDto;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.User;
import com.codingShuttle.SecurityApp.SecurityApplication.repositories.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final UserService userService;

    public LoginResponseDto login(LoginDto loginDto) {
        Authentication authentication =  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        User user = (User) authentication.getPrincipal();

        String accessToken =  jwtService.generateAccessToken(user);
        String refreshToken =  jwtService.generateRefreshToken(user);
        sessionService.generateNeSession(user,refreshToken);
        return new LoginResponseDto(user.getId() ,accessToken, refreshToken);

    }

//    public void logout(User user) {
//        sessionService.deleteByUser(user);
//    }

    public LoginResponseDto refreshToken(String refreshToken) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        sessionService.validateSession(refreshToken);
        User user = userService.getUserById(userId);
        String accessToken = jwtService.generateAccessToken(user);
        return new LoginResponseDto(user.getId() ,accessToken, refreshToken);
    }
}
