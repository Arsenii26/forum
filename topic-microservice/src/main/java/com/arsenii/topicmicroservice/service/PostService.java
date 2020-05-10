package com.arsenii.topicmicroservice.service;

import com.arsenii.topicmicroservice.model.Post;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {

    List<Post> allPosts();
    Page<Post> allPostsLimitedAscending(Integer pageNo, Integer pageSize, String sortBy);
    Page<Post> allPostsLimitedDescending(Integer pageNo, Integer pageSize, String sortBy);

    Post findPostByPostNumber(Long postId);

    Post savePost(Post post);

    void deleteById(Long postId);



}
