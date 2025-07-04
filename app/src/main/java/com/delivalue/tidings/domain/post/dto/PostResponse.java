package com.delivalue.tidings.domain.post.dto;

import com.delivalue.tidings.domain.data.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostResponse {
    private final String post_id;
    private final String user_id;
    private final String user_name;
    private final String profile_image;
    private final BadgeResponse badge;
    private final LocalDateTime create_at;
    private final ContentResponse content;
    private final Integer comment_count;
    private final Integer like_count;
    private final Integer scrap_count;
    private final boolean is_origin;

    //Required when isOrigin false
    private final String original_post_id;
    private final String original_user_id;

    public PostResponse(Post post) {
        this.post_id = post.getId();
        this.user_id = post.getUserId();
        this.user_name = post.getUserName();
        this.profile_image = post.getProfileImage();
        this.badge = new BadgeResponse().getBadgeResponse(post.getBadge());
        this.create_at = post.getCreatedAt();
        this.content = new ContentResponse().getContentResponse(post.getContent());
        this.comment_count = post.getCommentCount();
        this.like_count = post.getLikeCount();
        this.scrap_count = post.getScrapCount();
        this.is_origin = post.isOrigin();
        this.original_post_id = post.getOriginalPostId();
        this.original_user_id = post.getOriginalPostId();
    }

    @Getter
    public static class BadgeResponse {
        private Integer id;
        private String name;
        private String url;

        public BadgeResponse getBadgeResponse(Post.Badge badge) {
            if(badge == null) return null;

            this.id = badge.getId();
            this.name = badge.getName();
            this.url = badge.getUrl();
            return this;
        }
    }

    @Getter
    public static class ContentResponse {
        private String text;
        private List<PostMediaResponse> media;
        private List<String> tag;

        public ContentResponse getContentResponse(Post.Content content) {
            if(content == null) return null;

            this.text = content.getText();
            this.media = content.getMedia().stream()
                    .map(postMedia -> new PostMediaResponse().getPostMediaResponse(postMedia))
                    .collect(Collectors.toList());
            this.tag = content.getTag();
            return this;
        }
    }

    @Getter
    public static class PostMediaResponse {
        private String type; // image/video
        private String url;

        public PostMediaResponse getPostMediaResponse(Post.PostMedia postMedia) {
            if(postMedia == null) return null;

            this.type = postMedia.getType();
            this.url = postMedia.getUrl();
            return this;
        }
    }
}
