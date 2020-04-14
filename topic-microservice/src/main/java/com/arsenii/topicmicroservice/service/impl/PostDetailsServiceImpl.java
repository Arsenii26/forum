package com.arsenii.topicmicroservice.service.impl;

import com.arsenii.topicmicroservice.model.Post;
import com.arsenii.topicmicroservice.model.PostDetails;
import com.arsenii.topicmicroservice.repository.PostDetailsRepository;
import com.arsenii.topicmicroservice.service.PostDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostDetailsServiceImpl implements PostDetailsService {

    @Autowired
    private PostDetailsRepository postDetailsRepository;

    @Override
    public List<PostDetails> findAllPostsDetails() {
        return postDetailsRepository.findAll();
    }

    @Override
    public List<PostDetails> findAllPostsDetailsByNumber(Long postNumber) {
        return postDetailsRepository.findAllByPostNumber(postNumber);
    }


    @Override
    public PostDetails findPostDetailsByNumber(Long postNumber) {
        return postDetailsRepository.findById(postNumber).orElse(null);
    }

    @Override
    public PostDetails save(PostDetails postDetails) {
        return postDetailsRepository.save(postDetails);
    }

    @Override
    public PostDetails update(PostDetails postDetails) {
         return postDetailsRepository.save(postDetails);

    }

    @Override
    public void delete(Long number) {
        postDetailsRepository.deleteById(number);
    }
}
