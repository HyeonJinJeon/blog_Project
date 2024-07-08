package com.example.blog_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Followers")
@Getter
@Setter
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Follower() {
        this.createdAt = LocalDateTime.now();
    }

    public Follower(User user, User follower) {
        this();
        this.user = user;
        this.follower = follower;
    }
}
