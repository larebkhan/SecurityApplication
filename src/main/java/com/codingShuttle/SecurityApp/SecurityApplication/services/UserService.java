package com.codingShuttle.SecurityApp.SecurityApplication.services;

import com.codingShuttle.SecurityApp.SecurityApplication.dto.LoginDto;
import com.codingShuttle.SecurityApp.SecurityApplication.dto.SignUpDto;
import com.codingShuttle.SecurityApp.SecurityApplication.dto.UserDto;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.User;
import com.codingShuttle.SecurityApp.SecurityApplication.exceptions.ResourceNotFoundException;
import com.codingShuttle.SecurityApp.SecurityApplication.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new BadCredentialsException("User with email "+username+ "not found"));

    }

    public User getUserById(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User with id "+ userId + " not found"));
    }

    public UserDto signUp(SignUpDto signUpDto) {
        Optional<User> user =  userRepository.findByEmail(signUpDto.getEmail());
        if(user.isPresent()){
            System.out.println("User already exists: " + signUpDto.getEmail());  // Debugging
            throw new BadCredentialsException("User with email already exist" + signUpDto.getEmail());
        }
        User toCreate = modelMapper.map(signUpDto, User.class);
        toCreate.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        User savedUser =  userRepository.save(toCreate);
        return modelMapper.map(savedUser, UserDto.class);
    }
}
