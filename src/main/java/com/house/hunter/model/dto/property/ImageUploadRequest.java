package com.house.hunter.model.dto.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageUploadRequest {
    private MultipartFile[] images;

    public MultipartFile[] getImages() {
        return images;
    }

    public void setImages(MultipartFile[] images) {
        this.images = images;
    }
}

