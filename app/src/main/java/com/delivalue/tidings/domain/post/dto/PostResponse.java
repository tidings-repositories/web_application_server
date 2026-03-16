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
    private Integer view_count;
    private Integer repost_count;
    private boolean isOrigin;

    // Required when isOrigin false
    private String original_post_id;
    private String original_user_id;

    // Required when like post response
    private LocalDateTime like_at;

    // 기능 31: 게이밍 SNS 특화
    private String post_sub_type;
    private Long game_id;
    private List<String> game_genres;
    private List<String> game_platforms;
    private String game_version;

    // 기능 46: 대화 스레딩
    private String conversation_id;
    private String in_reply_to_post_id;

    // 기능 17: 가시성
    private String visibility_action;

    public PostResponse(Post post) {
        this.injectFromInterface(post);
        this.view_count = post.getViewCount();
        this.repost_count = post.getRepostCount();
        this.post_sub_type = post.getPostSubType();
        this.game_id = post.getGameId();
        this.game_genres = post.getGameGenres();
        this.game_platforms = post.getGamePlatforms();
        this.game_version = post.getGameVersion();
        this.conversation_id = post.getConversationId();
        this.in_reply_to_post_id = post.getInReplyToPostId();
        this.visibility_action = post.getVisibilityAction();
    }

    public PostResponse(PostSearch post) {
        this.injectFromInterface(post);
    }

    private void injectFromInterface(PostStructure post) {
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
        this.content = new ContentResponse().getContentResponse(
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
        private String type;
        private String url;

        public PostMediaResponse getPostMediaResponse(String type, String url) {
            this.type = type;
            this.url = url;
            return this;
        }
    }
}
