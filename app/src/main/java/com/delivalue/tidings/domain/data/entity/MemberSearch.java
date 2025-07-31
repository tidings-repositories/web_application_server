package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "member-index")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberSearch {
    @Id
    private String id;

    @Field(name = "badge_id", index = false)
    private Integer badge;

    @Field(name = "public_id", type = FieldType.Text)
    private String publicId;

    @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "korean")
    private String name;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String bio;

    @Field(name = "profile_image", index = false)
    private String profileImage;
    private String email;
    @Field(name = "following_count", index = false)
    private int followingCount;
    @Field(name = "follower_count", index = false)
    private int followerCount;
    @Field(name = "created_at")
    private LocalDateTime createdAt;
    @Field(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Field(name = "banned_at")
    private LocalDateTime bannedAt;
    @Field(name = "ban_reason", index = false)
    private String banReason;
}
