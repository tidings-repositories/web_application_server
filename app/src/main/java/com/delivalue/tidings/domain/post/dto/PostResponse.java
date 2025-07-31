package com.delivalue.tidings.domain.post.dto;

import com.delivalue.tidings.domain.data.entity.Post;
import com.delivalue.tidings.domain.data.entity.PostSearch;
import com.delivalue.tidings.domain.data.entity.interfaces.BadgeStructure;
import com.delivalue.tidings.domain.data.entity.interfaces.ContentStructure;
import com.delivalue.tidings.domain.data.entity.interfaces.PostStructure;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class PostResponse {
    private String post_id;
    private String user_id;
    private String user_name;
    private String profile_image;
    private BadgeResponse badge;
    private LocalDateTime create_at;
    private ContentResponse content;
    private Integer comment_count;
    private Integer like_count;
    private Integer scrap_count;
    private boolean isOrigin;

    //Required when isOrigin false
    private String original_post_id;
    private String original_user_id;

    //Required when like post response
    private LocalDateTime like_at;

    public PostResponse(Post post) {
        this.injection(post);
    }
    public PostResponse(PostSearch post) {
        this.injection(post);
    }

    private void injection(PostStructure post) {
        this.post_id = post.getId();
        this.user_id = post.getUserId();
        this.user_name = post.getUserName();
        this.profile_image = post.getProfileImage();

        BadgeStructure badgeSource = post.getBadge();
        this.badge = badgeSource != null
                ? new BadgeResponse().getBadgeResponse(badgeSource.getId(), badgeSource.getName(), badgeSource.getUrl())
                : null;

        this.create_at = post.getCreatedAt();

        ContentStructure contentSource = post.getContent();
        this.content =  new ContentResponse().getContentResponse(
                contentSource.getText(),
                contentSource.getMedia().stream()
                        .map(postMedia -> new PostMediaResponse().getPostMediaResponse(postMedia.getType(), postMedia.getUrl()))
                        .collect(Collectors.toList()),
                contentSource.getTag()
        );

        this.comment_count = post.getCommentCount();
        this.like_count = post.getLikeCount();
        this.scrap_count = post.getScrapCount();
        this.isOrigin = post.isOrigin();
        this.original_post_id = post.getOriginalPostId();
        this.original_user_id = post.getOriginalUserId();
    }

    @Getter
    public static class BadgeResponse {
        private Integer id;
        private String name;
        private String url;

        public BadgeResponse getBadgeResponse(Integer id, String name, String url) {
            this.id = id;
            this.name = name;
            this.url = url;
            return this;
        }
    }

    @Getter
    public static class ContentResponse {
        private String text;
        private List<PostMediaResponse> media;
        private List<String> tag;

        public ContentResponse getContentResponse(String text, List<PostMediaResponse> media, List<String> tag) {

            this.text = text;
            this.media = media;
            this.tag = tag;
            return this;
        }
    }

    @Getter
    public static class PostMediaResponse {
        private String type; // image/video
        private String url;

        public PostMediaResponse getPostMediaResponse(String type, String url) {
            this.type = type;
            this.url = url;
            return this;
        }
    }
}
