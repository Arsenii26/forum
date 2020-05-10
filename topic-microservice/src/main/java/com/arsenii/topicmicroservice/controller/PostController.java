package com.arsenii.topicmicroservice.controller;

import com.arsenii.topicmicroservice.intercomm.UserClient;
import com.arsenii.topicmicroservice.model.Post;
import com.arsenii.topicmicroservice.model.PostDetails;
import com.arsenii.topicmicroservice.model.User;
import com.arsenii.topicmicroservice.service.PostDetailsService;
import com.arsenii.topicmicroservice.service.PostService;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.springframework.boot.json.JsonParser;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController

public class PostController {


    @Autowired
    private UserClient userClient;

    @Autowired
    private PostService postService;

    @Autowired
    private PostDetailsService postDetailsService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private Environment environment;

    @Value("${spring.application.name}")
    private String serviceId;

    @GetMapping("/service/port")
    public String getPost(){
       return environment.getProperty("local.server.port");
    }

    @GetMapping("/service/instances")
    public ResponseEntity<?> getInstances() {
        return ResponseEntity.ok(discoveryClient.getInstances(serviceId));
    }

    /**
     * Posts methods
     */
    // you need create with pagination if you want to limit posts and not load the db
    @GetMapping("/service/all")
    public ResponseEntity<?> findAllPosts(){
        return ResponseEntity.ok(postService.allPosts());
    }
    @GetMapping("/service/allLimitedDesc")
    public ResponseEntity<?> findAllPostsDesc(@RequestParam(name = "page", defaultValue = "0") Integer pageNo,
                                              @RequestParam(name = "size", defaultValue = "5") Integer pageSize,
                                              @RequestParam(name = "sort", defaultValue = "post_number") String sortBy){
        return ResponseEntity.ok(postService.allPostsLimitedDescending(pageNo, pageSize, sortBy));
    }
    @GetMapping("/service/allLimitedAsc")
    public ResponseEntity<?> findAllPostsAsc(@RequestParam(name = "page", defaultValue = "0") Integer pageNo,
                                             @RequestParam(name = "size", defaultValue = "5") Integer pageSize,
                                             @RequestParam(name = "sort", defaultValue = "post_number") String sortBy){
        return ResponseEntity.ok(postService.allPostsLimitedAscending(pageNo, pageSize, sortBy));
    }


    @PostMapping("/service/post")
    public ResponseEntity<?> addPost(@RequestBody Post post){

        post.setDateCreated(LocalDateTime.now());
        return new ResponseEntity<>(postService.savePost(post), HttpStatus.CREATED);
    }

    @PutMapping("/service/post/{postNumber}")
    public ResponseEntity<?> updatePost(@RequestBody Post post, @PathVariable Long postNumber){
        post.setDateCreated(LocalDateTime.now());
        return new ResponseEntity<>(postService.savePost(post), HttpStatus.OK);
    }

    @DeleteMapping("/service/post/delete/{postNumber}")
//    @GetMapping("/service/post/delete/{postNumber}")  // cors work like this if aren't set up
    public ResponseEntity<?> deletePost(@PathVariable Long postNumber){

        postService.deleteById(postNumber);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/service/post/{postNumber}")
    public ResponseEntity<?> showPost(@PathVariable Long postNumber){

        return new ResponseEntity<>(postService.findPostByPostNumber(postNumber), HttpStatus.OK);
    }

    /**
     * Post details methods
     */
    @PostMapping("/service/post/{postNumber}/details")
    public ResponseEntity<?> addPostDetails(@RequestBody PostDetails postDetails, @PathVariable Long postNumber){

        postDetails.setDateCreated(LocalDateTime.now());
        postDetails.setPost(postService.findPostByPostNumber(postNumber));

        return new ResponseEntity<>(postDetailsService.save(postDetails), HttpStatus.CREATED);

    }

    @PutMapping("/service/post/{postNumber}/details/{number}")
    public ResponseEntity<?> updatePostDetails(@RequestBody PostDetails postDetails, @PathVariable Long postNumber, @PathVariable Long number){

        postDetails.setDateCreated(LocalDateTime.now());

        postDetails.setPost(postService.findPostByPostNumber(postNumber));

        return new ResponseEntity<>(postDetailsService.update(postDetails), HttpStatus.OK);
    }

    @DeleteMapping("/service/post/{postNumber}/details/delete/{number}")
    public ResponseEntity<?> deletePostDetails(@PathVariable Long postNumber, @PathVariable Long number){
        postDetailsService.delete(number);
        return ResponseEntity.noContent().build();
    }


    // this method uses communication with another microservice (user-microservice)
    @RequestMapping(value = "/service/post/{postNumber}/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllPostsDetails(@PathVariable Long postNumber) {
        List<PostDetails> postDetails = postDetailsService.findAllPostsDetailsByNumber(postNumber);

        // must use parallelStream because it should communicate in one thread
        List<Long> userIdList = postDetails.parallelStream().map(t -> t.getUserId()).collect(Collectors.toList());
//        System.out.println(userIdList.toString());
        List<User> users = userClient.findUsersIdByUserId(userIdList);

//        System.out.println(users.toString()); // print all taken users for debug

        // map for sending multiple Jackson responses
        Map<String, Object> result = new HashMap<String,Object>();
        result.put("postDetails",postDetails);
        result.put("users",users);
        // Add any additional props that you want to add
        return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
    }

    @GetMapping("/service/post/{postNumber}/details/{number}")
    public ResponseEntity<?> showPostDetails(@PathVariable Long postNumber, @PathVariable Long number){
        //we don't need postNumber cuz all numbers in details are unique
        return new ResponseEntity<>(postDetailsService.findPostDetailsByNumber(number), HttpStatus.OK);

    }
}
