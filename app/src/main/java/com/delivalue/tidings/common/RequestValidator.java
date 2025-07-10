package com.delivalue.tidings.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {
    @Value("${AWS_S3_MEDIA_BUCKET_NAME}")
    private String bucket;

    public boolean checkImageContentType(String contentType) {
        return contentType.startsWith("image/") && !contentType.contains("gif");
    }

    public boolean checkMediaContentType(String contentType) {
        return contentType.startsWith("image/") || contentType.startsWith("video/");
    }

    public boolean checkProfileUpdateParameter(String name, String bio, String profileImage) {
        if(name != null && name.length() > 12) return false;
        if(bio != null && bio.length() > 100) return false;
        if(profileImage != null && !profileImage.startsWith("https://" + this.bucket)) return false;

        return true;
    }
}
