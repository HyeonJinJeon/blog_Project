package com.example.blog_project.repository;

import com.example.blog_project.domain.Follower;
import com.example.blog_project.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {
    boolean existsByUserAndFollower(User user, User follower);
    void deleteByUserAndFollower(User user, User follower);
    long countByUser(User user);
    long countByFollower(User follower);
}
