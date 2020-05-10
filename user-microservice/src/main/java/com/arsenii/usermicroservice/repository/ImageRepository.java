package com.arsenii.usermicroservice.repository;

import com.arsenii.usermicroservice.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
