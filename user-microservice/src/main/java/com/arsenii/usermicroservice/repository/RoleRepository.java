package com.arsenii.usermicroservice.repository;


import com.arsenii.usermicroservice.model.Role;
import com.arsenii.usermicroservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
