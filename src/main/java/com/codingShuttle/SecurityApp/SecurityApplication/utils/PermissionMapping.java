package com.codingShuttle.SecurityApp.SecurityApplication.utils;

import static com.codingShuttle.SecurityApp.SecurityApplication.entities.enums.Roles.ADMIN;
import static com.codingShuttle.SecurityApp.SecurityApplication.entities.enums.Roles.CREATOR;
import static com.codingShuttle.SecurityApp.SecurityApplication.entities.enums.Roles.USER;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.codingShuttle.SecurityApp.SecurityApplication.entities.enums.Permissions;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.enums.Roles;
import static com.codingShuttle.SecurityApp.SecurityApplication.entities.enums.Permissions.*;

public class PermissionMapping {
    

    private static final Map<Roles, Set<Permissions>> map = Map.of(
        USER, Set.of(USER_VIEW,POST_VIEW),
        CREATOR, Set.of(USER_VIEW,POST_VIEW, USER_UPDATE,POST_UPDATE),
        ADMIN, Set.of(USER_DELETE,POST_DELETE, USER_CREATE, POST_CREATE, USER_UPDATE, POST_UPDATE)
    );

    public static Set<SimpleGrantedAuthority> getAuthoritiesForRole(Roles role){
        Set<SimpleGrantedAuthority> authorities = new java.util.HashSet<>();
        
        // Add the role authority (prefixed with "ROLE_")
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        
        // Add all permission authorities
        authorities.addAll(map.get(role).stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toSet()));
                
        return authorities;
    }

}
