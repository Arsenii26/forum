package com.arsenii.topicmicroservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_number", nullable = false, updatable = false, unique = true)
    private Long postNumber;

    private String name;

    private String description;

    private LocalDateTime dateCreated;


    private Long userId;
//https://stackoverflow.com/questions/36983215/failed-to-write-http-message-org-springframework-http-converter-httpmessagenotw
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post",
    cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
//    @JsonIgnore
    @JsonManagedReference
    private List<PostDetails> postDetails;

}
