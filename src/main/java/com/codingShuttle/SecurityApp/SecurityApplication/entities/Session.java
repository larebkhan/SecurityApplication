package com.codingShuttle.SecurityApp.SecurityApplication.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@AllArgsConstructor
@NoArgsConstructor

@Getter
@Setter
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false) // Creates a proper foreign key
    private User user;
    private String token;
    private LocalDateTime createdAt;
}
