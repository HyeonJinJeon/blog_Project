package com.example.blog_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "jwt_blacklist")
@Getter
@Setter
@NoArgsConstructor
public class JwtBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Column(name = "expiration_time")
    private Date expirationTime;

    public JwtBlacklist(String token, Date expirationTime) {
        this.token = token;
        this.expirationTime = expirationTime;
    }
}
