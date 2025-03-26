package com.codingShuttle.SecurityApp.SecurityApplication.filters;

import com.codingShuttle.SecurityApp.SecurityApplication.entities.Session;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.User;
import com.codingShuttle.SecurityApp.SecurityApplication.repositories.SessionRepository;
import com.codingShuttle.SecurityApp.SecurityApplication.services.JwtService;
import com.codingShuttle.SecurityApp.SecurityApplication.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@Component               // we need to create a bean for jwtAuthFilter
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    private final SessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            final String requestTokenHeader = request.getHeader("Authorization");
            if(requestTokenHeader == null || !requestTokenHeader.startsWith(("Bearer"))){
                filterChain.doFilter(request, response);
                return;
            }
            String token = requestTokenHeader.split("Bearer ")[1];
            Long userID = jwtService.getUserIdFromToken(token);
            if (userID != null && SecurityContextHolder.getContext().getAuthentication() == null){
                User user = userService.getUserById(userID);
                Optional<Session> sessionOpt = sessionRepository.findByRefreshToken(token);

                if (sessionOpt.isEmpty()) {
                    throw new RuntimeException("Invalid or expired token");
                }

                

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null,null);
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );//info about user device like ip address and some session related request, important for rate limiting or if you want to protect against a ddos attack

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request,response);
        }catch (Exception ex){
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }

    }
}
//context = different types of context contains different types of information of our application