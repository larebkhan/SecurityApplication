package com.codingShuttle.SecurityApp.SecurityApplication.filters;

import com.codingShuttle.SecurityApp.SecurityApplication.entities.User;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Collection;

@Component               // we need to create a bean for jwtAuthFilter
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;


    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            System.out.println("==== JWT Filter Start ====");
            System.out.println("Request URL: " + request.getRequestURI());
            
            if(requestTokenHeader == null || !requestTokenHeader.startsWith(("Bearer"))){
                System.out.println("No Bearer token found, skipping authentication");
                filterChain.doFilter(request, response);
                return;
            }

            String token = requestTokenHeader.split("Bearer ")[1];
            System.out.println("Token extracted: " + token.substring(0, Math.min(token.length(), 10)) + "...");
            
            Long userID = jwtService.getUserIdFromToken(token);
            System.out.println("User ID from token: " + userID);

            if (userID != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.getUserById(userID);
                
                Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
                System.out.println("User Roles: " + user.getRoles());
                System.out.println("Authorities: " + authorities);
                System.out.println("Has ROLE_ADMIN: " + authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(user, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("Authentication token set in SecurityContext");
            }

            System.out.println("Final Authentication: " + SecurityContextHolder.getContext().getAuthentication());
            System.out.println("==== JWT Filter End ====");
            filterChain.doFilter(request,response);
        } catch (Exception ex){
            System.out.println("Error in JWT Filter: " + ex.getMessage());
            ex.printStackTrace();
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
//context = different types of context contains different types of information of our application