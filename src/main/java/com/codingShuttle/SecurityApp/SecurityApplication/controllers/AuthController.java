package com.codingShuttle.SecurityApp.SecurityApplication.controllers;

import com.codingShuttle.SecurityApp.SecurityApplication.dto.LoginDto;
import com.codingShuttle.SecurityApp.SecurityApplication.dto.LoginResponseDto;
import com.codingShuttle.SecurityApp.SecurityApplication.dto.SignUpDto;
import com.codingShuttle.SecurityApp.SecurityApplication.dto.UserDto;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.Session;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.User;
import com.codingShuttle.SecurityApp.SecurityApplication.repositories.SessionRepository;
import com.codingShuttle.SecurityApp.SecurityApplication.services.AuthService;
import com.codingShuttle.SecurityApp.SecurityApplication.services.JwtService;
import com.codingShuttle.SecurityApp.SecurityApplication.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;
    private final SessionRepository sessionRepository;

    @Value("${deploy.env}")
    private String deployEnv;


    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpDto signUpDto){
        UserDto userDto = userService.signUp(signUpDto);
        return ResponseEntity.ok(userDto);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response){
        LoginResponseDto loginResponseDto = authService.login(loginDto);
        Cookie cookie = new Cookie("refreshToken", loginResponseDto.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure("production".equals(deployEnv));//to protect refresh token from being compromised
        response.addCookie(cookie);
        return ResponseEntity.ok(loginResponseDto);
    }

//    @PostMapping("/logout/")
//    public ResponseEntity<String> logout(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
//        }
//
//        String token = authHeader.split("Bearer ")[1]; // Remove "Bearer " prefix
//        Long userId = jwtService.getUserIdFromToken(token);
//        User user = userService.getUserById(userId);
//
//        authService.logout(user);
//        return ResponseEntity.ok("User logged out successfully");
//    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("No refresh token cookie found");
        }

        String refreshToken = Arrays.stream(cookies).
                filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found in cookies"));

        // Validate refresh token exists in session
        Optional<Session> sessionOpt = sessionRepository.findByRefreshToken(refreshToken);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        // Validate JWT signature and get user
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userService.getUserById(userId);

        // Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // Update session with new refresh token
        Session session = sessionOpt.get();
        session.setRefreshToken(newRefreshToken);
        sessionRepository.save(session);

        // Set new refresh token in cookie
        Cookie cookie = new Cookie("refreshToken", newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure("production".equals(deployEnv));
        response.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponseDto(user.getId(), newAccessToken, newRefreshToken));
    }
}
