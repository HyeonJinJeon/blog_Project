package com.example.blog_project.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "Series")
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    private String title;

    @Column(name = "cover_image_url")
    private String coverImageUrl;
}
