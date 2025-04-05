package com.codingShuttle.SecurityApp.SecurityApplication.entities;

import com.codingShuttle.SecurityApp.SecurityApplication.entities.enums.Roles;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.enums.SubscriptionPlans;
import com.codingShuttle.SecurityApp.SecurityApplication.utils.PermissionMapping;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    private String password;
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING) // to store roles as strings in database, otherwise they will be stored as numbers
    private Set<Roles> roles;

    @Column(nullable = false, columnDefinition = "integer default 2")
    @Builder.Default
    private int sessionLimit = 2; // Default session limit per user
    
    @Enumerated(EnumType.STRING) // Store plan as string
    @Builder.Default
    private SubscriptionPlans subscriptionPlan = SubscriptionPlans.FREE; // Ensure type is SubscriptionPlan
    
    public User(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        roles.forEach(role -> {
            Set<SimpleGrantedAuthority> permissions = PermissionMapping.getAuthoritiesForRole(role);
            authorities.addAll(permissions);
        });
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
