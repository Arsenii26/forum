package com.arsenii.topicmicroservice.service.impl;

import com.arsenii.topicmicroservice.model.Post;
import com.arsenii.topicmicroservice.repository.PostRepository;
import com.arsenii.topicmicroservice.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;


    @Override
    public List<Post> allPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post findPostByPostNumber(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public void deleteById(Long postId) {
        postRepository.deleteById(postId);
    }
}
