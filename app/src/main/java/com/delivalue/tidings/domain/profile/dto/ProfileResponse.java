package com.delivalue.tidings.domain.profile.dto;

import com.delivalue.tidings.domain.data.entity.Member;
import lombok.Getter;

@Getter
public class ProfileResponse {
    private final String user_id;
    private final String user_name;
    private final String bio;
    private final String profile_image;
    private final BadgeDto badge;
    private final int following_count;
    private final int follower_count;

    public ProfileResponse(Member member) {
        this.user_id = member.getPublicId();
        this.user_name = member.getName();
        this.bio = member.getBio();
        this.profile_image = member.getProfileImage();
        this.badge = member.getBadge() != null ? new BadgeDto(member.getBadge()) : null;
        this.following_count = member.getFollowingCount();
        this.follower_count = member.getFollowerCount();
    }
}
