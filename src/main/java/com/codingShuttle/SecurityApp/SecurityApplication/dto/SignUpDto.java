package com.codingShuttle.SecurityApp.SecurityApplication.dto;


import lombok.Data;

@Data
public class SignUpDto {
    private String email;
    private String password;
    private String name;
}
