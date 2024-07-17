package com.example.blog_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Tags")
@Getter
@Setter
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name" , nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tagSet")
    private Set<Post> posts = new HashSet<>();

    @ManyToMany(mappedBy = "tagSet")
    private Set<Draft> drafts = new HashSet<>();
}
