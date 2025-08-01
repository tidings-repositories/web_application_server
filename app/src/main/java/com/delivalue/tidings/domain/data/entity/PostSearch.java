package com.delivalue.tidings.domain.data.entity;

import com.delivalue.tidings.domain.data.entity.interfaces.BadgeStructure;
import com.delivalue.tidings.domain.data.entity.interfaces.ContentStructure;
import com.delivalue.tidings.domain.data.entity.interfaces.PostMediaStructure;
import com.delivalue.tidings.domain.data.entity.interfaces.PostStructure;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Document(indexName = "post-index")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostSearch implements PostStructure {
    @Id
    private String id;

    @Field(type = FieldType.Text, index = false)
    private String internalUserId;

    @Field(type = FieldType.Text)
    private String userId;
    @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "korean")
    private String userName;

    @Field(type = FieldType.Text, index = false)
    private String profileImage;
    @Field(type = FieldType.Object, index = false)
    private Badge badge;

    @Field(type = FieldType.Object)
    private Content content;

    @Field(type = FieldType.Integer, index = false)
    private Integer commentCount;
    @Field(type = FieldType.Integer, index = false)
    private Integer likeCount;
    @Field(type = FieldType.Integer, index = false)
    private Integer scrapCount;
    @Field(type = FieldType.Boolean, index = false)
    private boolean isOrigin;

    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Long createdAt;
    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Long deletedAt;
    @Field(type = FieldType.Text, index = false)
    private String originalPostId;
    @Field(type = FieldType.Text, index = false)
    private String originalUserId;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Badge implements BadgeStructure {
        @Field(name = "_id", type = FieldType.Integer, index = false)
        private Integer id;
        @Field(type = FieldType.Text, index = false)
        private String name;
        @Field(type = FieldType.Text, index = false)
        private String url;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Content implements ContentStructure {
        @Field(type = FieldType.Text, analyzer = "korean")
        private String text;
        @Field(type = FieldType.Object, index = false)
        private List<PostMedia> media;
        @Field(type = FieldType.Keyword)
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
        @Field(type = FieldType.Text, index = false)
        private String type; // image/video
        @Field(type = FieldType.Text, index = false)
        private String url;
    }

    @Transient
    public LocalDateTime getCreatedAt() {
        return Instant.ofEpochMilli(this.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Transient
    public LocalDateTime getDeletedAt() {
        return Instant.ofEpochMilli(this.deletedAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
