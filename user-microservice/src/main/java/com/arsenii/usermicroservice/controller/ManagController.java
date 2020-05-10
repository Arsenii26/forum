package com.arsenii.usermicroservice.controller;

import com.arsenii.usermicroservice.model.Role;
import com.arsenii.usermicroservice.model.User;
import com.arsenii.usermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manag")
//@PreAuthorize("hasRole('ROLE_ADMIN')")
public class ManagController {

    @Autowired
    private UserService userService;

    // find all users without Pagination
//    @GetMapping("/all")
//    public ResponseEntity<?> findAllUsers(){
//        return ResponseEntity.ok(userService.allUsers());
//    }


    @PutMapping("/{username}/enable")
    public void enableUser(@PathVariable("username") String username) {
        userService.enableUser(username);
    }

    @PutMapping("/{username}/disable")
    public void disableUser(@PathVariable("username") String username) {
        // finding the admin role
        Role role = userService.findByUsername(username).getRoles().stream()
                .filter(roleUser -> "role_admin".equalsIgnoreCase(roleUser.getName()))
                .findAny()
                .orElse(null);

        // if role == null means user isn't admin and we can disable
        if (role == null) {
            userService.disableUser(username);
        } else {
            // ideally throw exception and make output that can't disable admin accounts
        }
    }

    @GetMapping("/{username}/roleManagement")
    public ResponseEntity<?> displayRoles(@PathVariable("username") String username) {
        Map<String, List<Role>> map = new HashMap<>();
        map.put("userRoles", this.userService.findUserRoles(username));
        map.put("notUserRoles", this.userService.findNotUserRoles(username));
        System.out.println(map.toString());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/{username}/assignRole/{roleId}")
    public ResponseEntity<?> assignRole(@PathVariable("username") String username, @PathVariable("roleId") Integer roleId) {
        this.userService.assignRole(username, roleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{username}/removeRole/{roleId}")
    public ResponseEntity<?> removeRole(@PathVariable("username") String username, @PathVariable("roleId") Integer roleId) {
        this.userService.deleteRole(username, roleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
