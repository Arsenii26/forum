package com.arsenii.usermicroservice.service.Impl;

import com.arsenii.usermicroservice.dto.PasswordRenewDto;
import com.arsenii.usermicroservice.model.Role;
import com.arsenii.usermicroservice.model.User;
import com.arsenii.usermicroservice.model.token.VerificationToken;
import com.arsenii.usermicroservice.model.user.CrmUser;
import com.arsenii.usermicroservice.repository.RoleRepository;
import com.arsenii.usermicroservice.repository.UserRepository;
import com.arsenii.usermicroservice.repository.VerificationTokenRepository;
import com.arsenii.usermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    VerificationTokenRepository tokenRepository;

    @Override
    public User register(User user) {
        user.setUsername(user.getUsername().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmail(user.getEmail().toLowerCase());
        user.setEnabled(false);
        // give user default role for new user
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username){
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
//    public List<User> findApproxByUsername(String username) {
    public Page<User> findApproxByUsername(String username, Integer pageNo, Integer pageSize) {
        // this is working
//        return userRepository.findByUsernameContaining(username);
        return userRepository.findByUsernameContaining(username, PageRequest.of(pageNo, pageSize));
        // or like this
//        return userRepository.finApproxUserByUsername(username);
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
    public User updatePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteByUsername(Long userId, String username) {
        // delete role and token manually because they are depend
        this.userRepository.deleteRoleAndUserByUserId(userId);
        this.tokenRepository.deleteByUserId(userId);
        this.userRepository.deleteByUsername(username);
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

    // method checks that entered by user String is equals with the user password
    // if so, than returns true
    // if no, than returns false
    @Override
    public boolean checkPasswordsEquals(String username, String password) {
        User user = findByUsername(username);
        if (passwordEncoder.matches(password, user.getPassword())) {
            return true;
        }
        else {
            return false;
        }
    }


    @Override
    public User findUserDetail(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<Role> findUserRoles(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        return (List<Role>) user.getRoles();
    }

    @Override
    public List<Role> findNotUserRoles(String username) {
        User user = userRepository.findByUsername(username).orElse(null);

        List<Role> allRoles = (List<Role>) roleRepository.findAll();
        List<Role> userRoles = (List<Role>) user.getRoles();

        //remove all elements of second(user) list
        allRoles.removeAll(userRoles);

        return allRoles;
    }

    @Override
    public void assignRole(String username, long roleId) {
        User user = userRepository.findByUsername(username).orElse(null);

        // optional
        user.getRoles().add(roleRepository.findById(roleId).get());

        userRepository.save(user);
    }

    @Override
    public void deleteRole(String username, long roleId) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (roleRepository.findById(roleId).get().getName().toLowerCase().equals("role_user")) {
            // Validation: Can't remove user role from users!
//            System.out.println("Can't!");
        } else {
            user.getRoles().remove(roleRepository.findById(roleId).get());
        }

        userRepository.save(user);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(user, token);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }
}
