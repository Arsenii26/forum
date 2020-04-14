package com.arsenii.topicmicroservice.repository;

import com.arsenii.topicmicroservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserId(Long userId);
}
