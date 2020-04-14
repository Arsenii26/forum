package com.arsenii.usermicroservice.repository;

import com.arsenii.usermicroservice.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);

    @Query("select u.username from User u where u.userId in (:pIdList)")
    List<String> findByIdList(@Param("pIdList") List<Long> idList);

    @Query("from User u where u.userId in (:pIdList)")
    List<User> findUsersByIdList(@Param("pIdList") List<Long> idList);
}
