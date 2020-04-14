package com.arsenii.topicmicroservice.intercomm;

import com.arsenii.topicmicroservice.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Client for communicating with second microservice
 */
@FeignClient("user-service")
public interface UserClient {

    @RequestMapping(method = RequestMethod.POST, value = "/service/names", consumes = "application/json")
    List<String> getUserNames(@RequestBody List<Long> userIdList);

    @RequestMapping(method = RequestMethod.POST, value = "/service/users", consumes = "application/json")
    List<User> findUsersIdByUserId(@RequestBody List<Long> userIdList);

}
