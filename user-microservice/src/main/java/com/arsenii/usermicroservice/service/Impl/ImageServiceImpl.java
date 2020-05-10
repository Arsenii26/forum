package com.arsenii.usermicroservice.service.Impl;

import com.arsenii.usermicroservice.model.Image;
import com.arsenii.usermicroservice.repository.ImageRepository;
import com.arsenii.usermicroservice.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public Image createImage(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public Image findImageById(Long imageId) {
        return imageRepository.findById(imageId).orElse(null);
    }
}
