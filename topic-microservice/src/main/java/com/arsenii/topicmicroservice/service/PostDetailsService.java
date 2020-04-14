package com.arsenii.topicmicroservice.service;


import com.arsenii.topicmicroservice.model.Post;
import com.arsenii.topicmicroservice.model.PostDetails;

import java.util.List;

public interface PostDetailsService {

    List<PostDetails> findAllPostsDetails();

    List<PostDetails> findAllPostsDetailsByNumber(Long postNumber);

    PostDetails findPostDetailsByNumber(Long postNumber);

    PostDetails save(PostDetails postDetails);

    PostDetails update(PostDetails postDetails);

    void delete(Long number);

}
