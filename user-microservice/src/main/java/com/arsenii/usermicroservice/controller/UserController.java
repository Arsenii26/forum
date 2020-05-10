package com.arsenii.usermicroservice.controller;

import com.arsenii.usermicroservice.dto.PasswordRenewDto;
import com.arsenii.usermicroservice.event.OnRegistrationCompleteEvent;
import com.arsenii.usermicroservice.event.OnResetPasswordEvent;
import com.arsenii.usermicroservice.model.Image;
import com.arsenii.usermicroservice.model.Role;
import com.arsenii.usermicroservice.model.User;
import com.arsenii.usermicroservice.model.token.VerificationToken;
import com.arsenii.usermicroservice.model.user.CrmUser;
import com.arsenii.usermicroservice.service.ImageService;
import com.arsenii.usermicroservice.service.UserService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    private ImageService imageService;


    @Autowired
    private DiscoveryClient discoveryClient;


    // DISPLAY ALL list of all instances + ports
    @Autowired
    private Environment env;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Qualifier("messageSource")
    @Autowired
    private MessageSource messages;

    // get service id
    @Value("${spring.application.name}")
    private String serviceId;


    @GetMapping("/port")
    public String getPort() {
        return "Port number: " + env.getProperty("local.server.port");
    }

    @GetMapping("/services")
    public ResponseEntity<?> getServices() {
        return new ResponseEntity<>(discoveryClient.getServices(), HttpStatus.OK);
    }

    @GetMapping("/instances")
    public ResponseEntity<?> getInstances() {
        return new ResponseEntity<>(discoveryClient.getInstances(serviceId), HttpStatus.OK);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@RequestBody User user, HttpServletRequest request) {
        ResponseEntity response = null;
        User registered = null;
        if (userService.findByUsername(user.getUsername()) != null) {
            // 409
            response = new ResponseEntity<>("username exists", HttpStatus.CONFLICT);
        }
        if (userService.findByEmail(user.getEmail()) != null) {
            response = new ResponseEntity<>("email exists", HttpStatus.CONFLICT);
        }
        if (userService.findByUsername(user.getUsername()) == null && userService.findByEmail(user.getEmail()) == null) {
            //201
            registered = userService.register(user);
            try {

                String appUrl = request.getContextPath();
//                    String appUrl = "localhost:8000";
                eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                        request.getLocale(), appUrl));
                return new ResponseEntity<>(registered, HttpStatus.CREATED);

            } catch (RuntimeException ex) {
                response = new ResponseEntity<>("email error!", HttpStatus.CONFLICT);
                return response;
            }
        }
        return response;
    }

    @GetMapping("/login")
    public ResponseEntity<?> getUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            //This means; logout will be successful
            //200
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return ResponseEntity.ok(userService.findByUsername(principal.getName()));
    }

    @PostMapping("/names")
    public ResponseEntity<?> getNamesOfUsers(@RequestBody List<Long> idList) {
        return ResponseEntity.ok(userService.findUsers(idList));
    }

    @PostMapping("/users")
    public ResponseEntity<?> getUsers(@RequestBody List<Long> idList) {
        return ResponseEntity.ok(userService.getNamesOfUsers(idList));
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Working...!");
    }

    // find all users with Pagination
    @GetMapping("/all")
    public ResponseEntity<?> findAllUsers(@RequestParam(name = "page", defaultValue = "0") Integer pageNo,
                                          @RequestParam(name = "size", defaultValue = "10") Integer pageSize) {
        Page<User> list = userService.allUsersPage(pageNo, pageSize);

        return new ResponseEntity<Page<User>>(list, new HttpHeaders(), HttpStatus.OK);
    }


    @GetMapping("/findUser")
    public ResponseEntity<?> findUserIdByUsername(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(user.getUserId());
    }

    @GetMapping("/findUserById")
    public ResponseEntity<?> findUserIdByUserId(Long userId) {
        User user = userService.findByUserId(userId);
        return ResponseEntity.ok(user);
    }


    @GetMapping("/findUserByUsername")
    public ResponseEntity<?> findUserByUsername(
            @RequestParam("username") String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @GetMapping("/findApproxUserByUsername")
    public ResponseEntity<?> findApproxUserByUsername(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "page", defaultValue = "0") Integer pageNo,
            @RequestParam(name = "size", defaultValue = "10") Integer pageSize) {
        Page<User> list = userService.findApproxByUsername(username, pageNo, pageSize);

        return new ResponseEntity<Page<User>>(list, new HttpHeaders(), HttpStatus.OK);
    }


    @PutMapping("/{username}/accountInfo")
    public ResponseEntity<?> changeUserDetails(@RequestBody User currentUser,
                                               @PathVariable("username") String username) {

        User userTemp = userService.findByUsername(currentUser.getUsername());

        userTemp.setFirstName(currentUser.getFirstName());
        userTemp.setLastName(currentUser.getLastName());
        userTemp.setPhone(currentUser.getPhone());

        return new ResponseEntity<>(userService.update(userTemp), HttpStatus.OK);
    }


    @PutMapping("/{username}/changePassword")
    public ResponseEntity<?> changeUserPassword(@PathVariable("username") String username, @RequestBody PasswordRenewDto passwordRenewDto) {

        try {
            userService.changeUserPassword(username, passwordRenewDto);
        } catch (InputMismatchException e) {
            return new ResponseEntity<>("User password not matches with entered!", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping("/{username}")
    public ResponseEntity<?> showUserDetails(@PathVariable String username) {
        return new ResponseEntity<>(userService.findUserDetail(username), HttpStatus.OK);
    }


    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable("username") String username, @RequestParam("enteredPassword") String enteredPassword) {
        User user = this.userService.findByUsername(username);
        if (this.userService.checkPasswordsEquals(username, enteredPassword)) {
            this.userService.deleteByUsername(user.getUserId(), user.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Passwords are not equals", HttpStatus.CONFLICT);
        }
    }

//https://www.baeldung.com/registration-verify-user-by-email
    @PutMapping("/registrationConfirm")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {


        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            return new ResponseEntity<>("Invalid token", new HttpHeaders(), HttpStatus.CONFLICT);
        }

        User user = verificationToken.getUser();
        // if I didn't set the time of expiration
        try {
            Calendar cal = Calendar.getInstance();
            if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
                return new ResponseEntity<>("Token been expired", new HttpHeaders(), HttpStatus.CONFLICT);
            }
        } catch (NullPointerException e) {
            //NOP
        }

        user.setEnabled(true);
        this.userService.update(user);
        return new ResponseEntity<>(user.isEnabled(), new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email, WebRequest request) {
        if (userService.findByEmail(email) == null) {
            // 409
            return new ResponseEntity<>("email not exists", HttpStatus.CONFLICT);
        }
        //201
        User existedUser = userService.findByEmail(email);

        // create random password
        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        existedUser.setPassword(generatedString);
        this.userService.updatePassword(existedUser);

        try {
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnResetPasswordEvent(existedUser,
                    request.getLocale(), appUrl, generatedString));
            return new ResponseEntity<>(existedUser, HttpStatus.CREATED);

        } catch (RuntimeException ex) {
            return new ResponseEntity<>("Unknown error", HttpStatus.CONFLICT);
        }
    }


    // method will update user image (url to image)
    @PutMapping("/{username}/setImg")
    public ResponseEntity<?> updateImage(@PathVariable("username") String username,
                                   @RequestParam("imageUrl") String imageUrl) {

        try {

        } catch (Exception e) {
            return new ResponseEntity<>("Unknown error", HttpStatus.CONFLICT);
        }

        // enabling boolean that picture was set in course table in db
        User userTemp = userService.findByUsername(username);
        userTemp.setPicture(true);


        try {
            Image imageExist = imageService.findImageById(userTemp.getImage().getImageId());
            // if image was assigned
            if (imageExist != null) {
                // set bytes to blob
                imageExist.setImage(imageUrl);
                userTemp.setImage(imageExist);
            }
        } catch (NullPointerException e) {
            // insert new image
            Image image = new Image();
            image.setImage(imageUrl);
            userTemp.setImage(image);
        }

        return new ResponseEntity<>(userService.update(userTemp), HttpStatus.OK);
    }
}
