package com.delivalue.tidings.domain.post.dto;

import com.delivalue.tidings.domain.data.entity.Post;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class PostCreateRequest {
    private String id;
    private String internalUserId;
    private String userId;
    private String userName;
    private String profileImage;
    private Post.Badge badge;
    private LocalDateTime createdAt;
    private Post.Content content;
    private Integer commentCount;
    private Integer likeCount;
    private Integer scrapCount;
    private boolean isOrigin;

    public PostCreateRequest(Post.Content requestBody) {
        if(!requestBody.getMedia().isEmpty()) {
            String CDN_ORIGIN = "https://cdn.stellagram.kr";
            List<Post.PostMedia> mediaList = requestBody.getMedia();
            mediaList.forEach(media -> {
                String beforeProcessUrl = media.getUrl();
                String path = URI.create(beforeProcessUrl).getPath();
                media.setUrl(CDN_ORIGIN + path);
            });
        }

        this.id = UUID.randomUUID().toString();
        this.content = requestBody;
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.commentCount = 0;
        this.likeCount = 0;
        this.scrapCount = 0;
        this.isOrigin = true;
    }

    public Post toEntity() {
        return Post.builder().id(this.id).internalUserId(this.internalUserId)
                .userId(this.userId).userName(this.userName)
                .profileImage(this.profileImage).badge(this.badge)
                .createdAt(this.createdAt).content(this.content)
                .commentCount(this.commentCount).likeCount(this.likeCount).scrapCount(this.scrapCount)
                .isOrigin(this.isOrigin).build();
    }
}
