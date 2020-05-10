package com.arsenii.usermicroservice.service;

import com.arsenii.usermicroservice.model.Image;

public interface ImageService {

    Image createImage(Image image);

    Image findImageById(Long imageId);
}
