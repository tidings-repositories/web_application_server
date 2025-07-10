package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    private String id;

    private String internalUserId;
    private String userId;
    private String userName;
    private String profileImage;
    private Badge badge;
    private Content content;
    private Integer commentCount;
    private Integer likeCount;
    private Integer scrapCount;
    private boolean isOrigin;
    private LocalDateTime createdAt;

    @Field(write = Field.Write.NON_NULL)
    private LocalDateTime deletedAt;

    @Field(write = Field.Write.NON_NULL)
    private String originalPostId;
    @Field(write = Field.Write.NON_NULL)
    private String originalUserId;

    @Getter
    @Setter
    public static class Badge {
        private Integer id;
        private String name;
        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private String text;
        private List<PostMedia> media;
        private List<String> tag;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostMedia {
        private String type; // image/video
        private String url;
    }
}
