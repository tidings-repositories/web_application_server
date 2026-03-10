package com.delivalue.tidings.domain.profile.dto;

import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.entity.MemberSearch;
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
    private final boolean is_verified;
    private final String user_state;
    private final String country_code;
    private final String language_code;

    public ProfileResponse(Member member) {
        this.user_id = member.getPublicId();
        this.user_name = member.getName();
        this.bio = member.getBio();
        this.profile_image = member.getProfileImage();
        this.badge = member.getBadge() != null ? new BadgeDto(member.getBadge()) : null;
        this.following_count = member.getFollowingCount();
        this.follower_count = member.getFollowerCount();
        this.is_verified = Boolean.TRUE.equals(member.getIsVerified());
        this.user_state = member.getUserState();
        this.country_code = member.getCountryCode();
        this.language_code = member.getLanguageCode();
    }

    public ProfileResponse(MemberSearch member, BadgeDto badge) {
        this.user_id = member.getPublicId();
        this.user_name = member.getName();
        this.bio = member.getBio();
        this.profile_image = member.getProfileImage();
        this.badge = badge;
        this.following_count = member.getFollowingCount();
        this.follower_count = member.getFollowerCount();
        this.is_verified = false;
        this.user_state = "NORMAL";
        this.country_code = null;
        this.language_code = null;
    }
}
