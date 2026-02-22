package com.delivalue.tidings.domain.post.dto;

import com.delivalue.tidings.domain.data.entity.Post;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
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

    public PostCreateRequest(PostContentRequest requestBody) {
        Post.Content postContent = new Post.Content();
        postContent.setText(requestBody.getText());
        postContent.setTag(requestBody.getTag());

        List<Post.PostMedia> mediaList = new ArrayList<>();
        if (requestBody.getMedia() != null) {
            String CDN_ORIGIN = "https://cdn.stellagram.kr";
            for (PostContentRequest.PostMediaItem item : requestBody.getMedia()) {
                Post.PostMedia postMedia = new Post.PostMedia();
                postMedia.setType(item.getType());
                String path = URI.create(item.getUrl()).getPath();
                postMedia.setUrl(CDN_ORIGIN + path);
                mediaList.add(postMedia);
            }
        }
        postContent.setMedia(mediaList);

        this.id = UUID.randomUUID().toString();
        this.content = postContent;
        this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
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
