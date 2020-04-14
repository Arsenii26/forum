package com.arsenii.usermicroservice.service;

import com.arsenii.usermicroservice.dto.PasswordRenewDto;
import com.arsenii.usermicroservice.model.User;
import com.arsenii.usermicroservice.model.user.CrmUser;
import com.arsenii.usermicroservice.repository.RoleRepository;
import com.arsenii.usermicroservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // give user default role for new user
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username){
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findByUserId(Long userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public List<String> findUsers(List<Long> idList){
        return userRepository.findByIdList(idList);
    }

    @Override
    public List<User> getNamesOfUsers(List<Long> idList) {
        return userRepository.findUsersByIdList(idList);
    }

    @Override
    public List<User> allUsers() {
        return userRepository.findAll();
    }


    @Override
    public Page<User> allUsersPage(Integer pageNo, Integer pageSize) {
        // find all pages without sort by
        return userRepository.findAll(PageRequest.of(pageNo, pageSize));
    }

    @Override
    public void enableUser(String username) {
        User user = findByUsername(username);
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disableUser(String username) {
        User user = findByUsername(username);
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public User update(User user) {
      return userRepository.save(user);
    }

    @Override
    public void changeUserPassword(String username, PasswordRenewDto passwordRenewDto) {
        User user = findByUsername(username);

        boolean result = passwordEncoder.matches(passwordRenewDto.getOldPassword(), user.getPassword());
        //trying to match entered user password and real user password
        if (result == true) {
            // if and password matches than trying to match (new password with confirmed)
            // this will be always true because I have same validation on the front-end side
            if(passwordRenewDto.getPassword().equals(passwordRenewDto.getConfirmPassword())){

                //encode new password
                user.setPassword(passwordEncoder.encode(passwordRenewDto.getPassword()));
                //update
                userRepository.save(user);
            }
            else {
//               throw new Ex
                throw new InputMismatchException();
            }
        } else {
            throw new InputMismatchException();
        }
    }

    @Override
    public User findUserDetail(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
