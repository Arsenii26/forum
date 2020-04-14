package com.arsenii.usermicroservice.controller;

import com.arsenii.usermicroservice.dto.PasswordRenewDto;
import com.arsenii.usermicroservice.model.Role;
import com.arsenii.usermicroservice.model.User;
import com.arsenii.usermicroservice.model.user.CrmUser;
import com.arsenii.usermicroservice.service.UserService;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/service")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DiscoveryClient discoveryClient;


    // DISPLAY ALL list of all instances + ports
    @Autowired
    private Environment env;

    // get service id
    @Value("${spring.application.name}")
    private String serviceId;


    @GetMapping("/port")
    public  String getPort(){
        return "Port number: " + env.getProperty("local.server.port");
    }

    @GetMapping("/services")
    public ResponseEntity<?> getServices(){
        return new ResponseEntity<>(discoveryClient.getServices(), HttpStatus.OK);
    }

    @GetMapping("/instances")
    public ResponseEntity<?> getInstances(){
        return new ResponseEntity<>(discoveryClient.getInstances(serviceId), HttpStatus.OK);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@RequestBody User user, BindingResult theBindingResult){
        ResponseEntity response = null;
            if(userService.findByUsername(user.getUsername()) != null){
                // 409
//                return new ResponseEntity<>("username exists", HttpStatus.CONFLICT);
                response = new ResponseEntity<>("username exists", HttpStatus.CONFLICT);
            }
            if (userService.findByEmail(user.getEmail()) != null) {
//                return new ResponseEntity<>("email exists", HttpStatus.CONFLICT);
                response = new ResponseEntity<>("email exists", HttpStatus.CONFLICT);
            }
            if (userService.findByUsername(user.getUsername()) == null && userService.findByEmail(user.getEmail()) == null) {
                //201
                return new ResponseEntity<>(userService.register(user), HttpStatus.CREATED);
            }
        return response;
//        if (theBindingResult.hasErrors()) {
//            // HERE VALIDATION OF THE EMAIL
//            return new ResponseEntity<>(HttpStatus.CONFLICT);
//        }
        }

    @GetMapping("/login")
    public ResponseEntity<?> getUser(Principal principal){
        if(principal == null || principal.getName() == null){
            //This means; logout will be successful
            //200
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return ResponseEntity.ok(userService.findByUsername(principal.getName()));
    }

    @PostMapping("/names")
    public ResponseEntity<?> getNamesOfUsers(@RequestBody List<Long> idList){
        return ResponseEntity.ok(userService.findUsers(idList));
    }

    @PostMapping("/users")
    public ResponseEntity<?> getUsers(@RequestBody List<Long> idList){
        return ResponseEntity.ok(userService.getNamesOfUsers(idList));
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok("Working...!");
    }

// find all users without Pagination
//    @GetMapping("/all")
////    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<?> findAllUsers(){
//        return ResponseEntity.ok(userService.allUsers());
//    }

    // find all users with Pagination
    @GetMapping("/all")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> findAllUsers(@RequestParam(name = "page", defaultValue = "0") Integer pageNo,
                                          @RequestParam(name = "size", defaultValue = "10") Integer pageSize){
//        List<User> list = userService.allUsersPage(pageNo, pageSize);
        Page<User> list = userService.allUsersPage(pageNo, pageSize);

        return new ResponseEntity<Page<User>>(list, new HttpHeaders(), HttpStatus.OK);
    }


    @GetMapping("/findUser")
    public ResponseEntity<?> findUserIdByUsername(Principal principal){
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(user.getUserId());
    }

    @GetMapping("/findUserById")
    public ResponseEntity<?> findUserIdByUserId(Long userId){
        User user = userService.findByUserId(userId);
        return ResponseEntity.ok(user);
    }


    @GetMapping("/findUserByUsername")
    public ResponseEntity<?> findUserByUsername(
            @RequestParam("username") String username ){
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @PutMapping("/{username}/enable")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void enableUser(@PathVariable("username") String username) {
        userService.enableUser(username);
    }

    @PutMapping("/{username}/disable")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void disableUser(@PathVariable("username") String username) {
        // finding the admin role
        Role role = userService.findByUsername(username).getRoles().stream()
                .filter(roleUser -> "role_admin".equalsIgnoreCase(roleUser.getName()))
                .findAny()
                .orElse(null);

        // if role == null means user isn't admin and we can disable
        if(role == null){
            userService.disableUser(username);
        } else {
            // ideally throw exception and make output that can't disable admin accounts
        }
    }

    @PutMapping("/{username}/accountInfo")
    public  ResponseEntity<?> changeUserDetails(@RequestBody User currentUser, @PathVariable("username") String username){

        User userTemp = userService.findByUsername(currentUser.getUsername());

        // hidden
        userTemp.setUserId(currentUser.getUserId());

        // can be change

        userTemp.setFirstName(currentUser.getFirstName());
        userTemp.setLastName(currentUser.getLastName());
        userTemp.setEmail(currentUser.getEmail());
        userTemp.setPhone(currentUser.getPhone());

        // not lost it
        userTemp.setUsername(userTemp.getUsername());
        userTemp.setPassword(userTemp.getPassword());
        userTemp.setEnabled(userTemp.isEnabled());

        userTemp.setRoles(userTemp.getRoles());

        return new ResponseEntity<>(userService.update(userTemp), HttpStatus.OK);
    }


    @PutMapping("/{username}/changePassword")
    public ResponseEntity<?> changeUserPassword(@PathVariable("username") String username, @RequestBody PasswordRenewDto passwordRenewDto){

        try {
            userService.changeUserPassword(username, passwordRenewDto);
        }
        catch (InputMismatchException e) {
            return new ResponseEntity<>("User password not matches with entered!", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping("/{username}")
    public ResponseEntity<?> showUserDetails(@PathVariable String username){
        return new ResponseEntity<>(userService.findUserDetail(username), HttpStatus.OK);
    }
}
