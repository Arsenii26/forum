package com.arsenii.topicmicroservice.service;

import com.arsenii.topicmicroservice.model.Post;

import java.util.List;

public interface PostService {

    List<Post> allPosts();

    Post findPostByPostNumber(Long postId);

    Post savePost(Post post);

    void deleteById(Long postId);



}
