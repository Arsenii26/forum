package com.arsenii.usermicroservice.service;

import com.arsenii.usermicroservice.dto.PasswordRenewDto;
import com.arsenii.usermicroservice.model.Role;
import com.arsenii.usermicroservice.model.User;
import com.arsenii.usermicroservice.model.token.VerificationToken;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    User register(User user);

    User findByUsername(String username); // exact search

    Page<User> findApproxByUsername(String username, Integer pageNo, Integer pageSize); // approx search (by username)
//    List<User> findApproxByUsername(String username);

    User findByEmail(String email);

    User findByUserId(Long userId);

    List<String> findUsers(List<Long> idList); // for the second microservice

    List<User> getNamesOfUsers(List<Long> idList); // get all users

    List<User> allUsers();

    Page<User> allUsersPage(Integer pageNo, Integer pageSize); // get all users with pages

    void enableUser (String username);

    void disableUser (String username);

    User update(User user);

    User updatePassword(User user);

    void deleteByUsername(Long userId, String username);

    void changeUserPassword(String username, PasswordRenewDto passwordRenewDto);

    boolean checkPasswordsEquals(String username, String password);

    User findUserDetail(String username);

    List<Role> findUserRoles(String username);

    List<Role> findNotUserRoles(String username);

    void assignRole(String username, long roleId);

    void deleteRole(String username, long roleId);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);
}
