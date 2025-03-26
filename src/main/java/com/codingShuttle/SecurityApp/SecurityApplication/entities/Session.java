package com.codingShuttle.SecurityApp.SecurityApplication.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false) // Creates a proper foreign key
    private User user;
    private String refreshToken;

    @CreationTimestamp
    private LocalDateTime lastUsedAt;
}
