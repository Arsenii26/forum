package com.arsenii.topicmicroservice.repository;

import com.arsenii.topicmicroservice.model.PostDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostDetailsRepository extends JpaRepository<PostDetails, Long> {
    @Query(" from PostDetails pd where pd.post.postNumber in (:postNumber)")
    List<PostDetails> findAllByPostNumber(Long postNumber);
}
