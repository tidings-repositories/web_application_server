package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    private ObjectId id;

    private String postId;
    private String userId;
    private String internalUserId;
    private String userName;
    private String profileImage;
    private String text;
    private Badge badge;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    private boolean isRoot;
    @Field(write = Field.Write.NON_NULL)
    private ObjectId rootCommentId;

    // 기능 20: VisibilityLib 댓글 섹션 분류 (DOC_08/28)
    @Field(write = Field.Write.NON_NULL)
    private Double toxicityScore;

    @Builder.Default
    private String visibilityAction = "ALLOW"; // ALLOW / LOW_QUALITY_SECTION / ABUSIVE_QUALITY_SECTION / DROP

    @Getter
    @Setter
    public static class Badge {
        private Integer id;
        private String name;
        private String url;
    }
}
