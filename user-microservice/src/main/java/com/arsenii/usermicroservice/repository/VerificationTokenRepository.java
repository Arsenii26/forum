package com.arsenii.usermicroservice.repository;

import com.arsenii.usermicroservice.model.User;
import com.arsenii.usermicroservice.model.token.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);

    // https://stackoverflow.com/questions/1905607/cannot-issue-data-manipulation-statements-with-executequery
    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.user.userId in (:userId)")
    void deleteByUserId(@Param("userId") Long userId);
}