package com.arsenii.usermicroservice.service;

import com.arsenii.usermicroservice.dto.PasswordRenewDto;
import com.arsenii.usermicroservice.model.User;
import com.arsenii.usermicroservice.model.user.CrmUser;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    User register(User user);

    User findByUsername(String username);

    User findByEmail(String email);

    User findByUserId(Long userId);

    List<String> findUsers(List<Long> idList);

    List<User> getNamesOfUsers(List<Long> idList);

    List<User> allUsers();

    Page<User> allUsersPage(Integer pageNo, Integer pageSize);

    void enableUser (String username);

    void disableUser (String username);

    User update(User user);

    void changeUserPassword(String username, PasswordRenewDto passwordRenewDto);

    User findUserDetail(String username);
}
