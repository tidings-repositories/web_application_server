package com.delivalue.tidings.domain.profile.dto;

import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@Setter
@Getter
public class ProfileUpdateRequest {
    private final String id;
    private final String userName;
    private final String bio;
    private final String profileImage;
    private Integer badgeId;

    public ProfileUpdateRequest(String id, String userName, String bio, String profileImage, Integer badgeId) {
        this.id = id;
        this.userName = userName;
        this.bio = bio;
        this.badgeId = badgeId;

        if(profileImage != null) {
            String CDN_ORIGIN = "https://cdn.stellagram.kr";
            String path = URI.create(profileImage).getPath();
            this.profileImage = CDN_ORIGIN + path;
        } else this.profileImage = null;
    }
}
