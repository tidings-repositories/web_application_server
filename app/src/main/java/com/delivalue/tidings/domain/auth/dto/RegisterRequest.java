package com.delivalue.tidings.domain.auth.dto;

import com.delivalue.tidings.domain.data.entity.Member;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
public class RegisterRequest {
    private final String internalId;
    private final String publicId;
    private final String name;
    private final String email;

    public RegisterRequest(String internalId, String publicId, String name, String email) {
        this.internalId = internalId;
        this.publicId = publicId;
        this.name = name;
        this.email = email;
    }

    public Member toEntity() {
        return Member.builder()
                .id(this.internalId)
                .publicId(this.publicId)
                .name(this.name)
                .bio("")
                .profileImage("https://cdn.stellagram.kr/public/defaultProfile.png") //default profile image
                .badge(null)
                .email(this.email)
                .followerCount(0)
                .followingCount(1) //stellagram official
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .deletedAt(null)
                .build();
    }
}