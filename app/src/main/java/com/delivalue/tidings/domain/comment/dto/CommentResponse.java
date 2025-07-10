package com.delivalue.tidings.domain.comment.dto;

import com.delivalue.tidings.domain.data.entity.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CommentResponse {
    private String comment_id;
    private String post_id;
    private String user_id;
    private String user_name;
    private String profile_image;
    private String text;
    private Comment.Badge badge;
    private LocalDateTime create_at;

    private boolean isDeleted;
    private boolean isRoot;
    private List<CommentResponse> reply;

    public CommentResponse(Comment comment) {
        this.comment_id = comment.getId().toString();
        this.post_id = comment.getPostId();
        this.user_id = comment.getUserId();
        this.user_name = comment.getUserName();;
        this.profile_image = comment.getProfileImage();
        this.text = comment.getText();
        this.badge = comment.getBadge();;
        this.create_at = comment.getCreatedAt();
        this.isRoot = comment.isRoot();
        if(comment.isRoot()) this.reply = new ArrayList<CommentResponse>();

        if(comment.getDeletedAt() != null) {
            this.isDeleted = true;
            this.user_name = "";
            this.text = "삭제된 코멘트입니다";
            this.profile_image = "https://cdn.stellagram.kr/public/defaultProfile.png";
            this.badge = null;
        }
    }
}
