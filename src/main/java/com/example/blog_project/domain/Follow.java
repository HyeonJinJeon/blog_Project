package com.example.blog_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Follows")
@Getter
@Setter
@NoArgsConstructor
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "blog_user_id", nullable = false)
    private User blogUser;

    @ManyToOne
    @JoinColumn(name = "current_user_id", nullable = false)
    private User currentUser;
}
