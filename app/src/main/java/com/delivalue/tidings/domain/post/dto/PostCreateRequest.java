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

    // 기능 31: 게이밍 SNS 특화
    private String postSubType;
    private Long gameId;
    private List<String> gameGenres;
    private List<String> gamePlatforms;
    private String gameVersion;

    // 기능 46: 대화 스레딩
    private String inReplyToPostId;
    private String inReplyToMemberId;
    private String conversationId;

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

        this.postSubType = requestBody.getPostSubType() != null ? requestBody.getPostSubType() : "TEXT";
        this.gameId = requestBody.getGameId();
        this.gameGenres = requestBody.getGameGenres() != null ? requestBody.getGameGenres() : new ArrayList<>();
        this.gamePlatforms = requestBody.getGamePlatforms() != null ? requestBody.getGamePlatforms() : new ArrayList<>();
        this.gameVersion = requestBody.getGameVersion();
        this.inReplyToPostId = requestBody.getInReplyToPostId();
    }

    public Post toEntity() {
        boolean hasMedia = this.content.getMedia() != null && !this.content.getMedia().isEmpty();
        boolean isReply = this.inReplyToPostId != null;

        return Post.builder()
                .id(this.id)
                .internalUserId(this.internalUserId)
                .userId(this.userId)
                .userName(this.userName)
                .profileImage(this.profileImage)
                .badge(this.badge)
                .createdAt(this.createdAt)
                .content(this.content)
                .commentCount(this.commentCount)
                .likeCount(this.likeCount)
                .scrapCount(this.scrapCount)
                .isOrigin(this.isOrigin)
                .viewCount(0)
                .repostCount(0)
                .hasMedia(hasMedia)
                .isReply(isReply)
                .isRetweet(false)
                .postSubType(this.postSubType)
                .gameId(this.gameId)
                .gameGenres(this.gameGenres)
                .gamePlatforms(this.gamePlatforms)
                .gameVersion(this.gameVersion)
                .inReplyToPostId(this.inReplyToPostId)
                .inReplyToMemberId(this.inReplyToMemberId)
                .conversationId(isReply ? this.inReplyToPostId : this.id) // 루트면 자신이 conversationId
                .conversationDepth(isReply ? 1 : 0)
                .visibilityAction("ALLOW")
                .build();
    }
}
