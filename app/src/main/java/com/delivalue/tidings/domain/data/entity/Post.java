package com.delivalue.tidings.domain.data.entity;

import com.delivalue.tidings.domain.data.entity.interfaces.BadgeStructure;
import com.delivalue.tidings.domain.data.entity.interfaces.ContentStructure;
import com.delivalue.tidings.domain.data.entity.interfaces.PostMediaStructure;
import com.delivalue.tidings.domain.data.entity.interfaces.PostStructure;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post implements PostStructure {

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
    @NoArgsConstructor
    public static class Badge implements BadgeStructure {
        private Integer id;
        private String name;
        private String url;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Content implements ContentStructure {
        private String text;
        private List<PostMedia> media;
        private List<String> tag;

        @Override
        public List<PostMediaStructure> getMedia() {
            return new ArrayList<>(media);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PostMedia implements PostMediaStructure {
        private String type; // image/video
        private String url;
    }
}
