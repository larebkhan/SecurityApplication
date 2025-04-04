package com.codingShuttle.SecurityApp.SecurityApplication.dto;


import com.codingShuttle.SecurityApp.SecurityApplication.entities.enums.Permissions;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.enums.Roles;
import lombok.Data;

import java.util.Set;

@Data
public class SignUpDto {
    private String email;
    private String password;
    private String name;
    private Set<Roles> roles;
    private Set<Permissions> permissions;
}
