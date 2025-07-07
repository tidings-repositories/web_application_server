package com.delivalue.tidings.domain.comment.service;

import com.delivalue.tidings.domain.comment.dto.CommentCreateRequest;
import com.delivalue.tidings.domain.comment.dto.CommentResponse;
import com.delivalue.tidings.domain.data.entity.Badge;
import com.delivalue.tidings.domain.data.entity.Comment;
import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.entity.Post;
import com.delivalue.tidings.domain.data.repository.CommentRepository;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final MongoTemplate mongoTemplate;

    public List<CommentResponse> getPostComment(String postId) {
        Map<String, CommentResponse> result = new LinkedHashMap<>();
        List<Comment> thisPostCommentList = this.commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        //수집
        for(Comment comment : thisPostCommentList) {
            if(comment.getDeletedAt() != null && !comment.isRoot()) continue;

            CommentResponse thisComment = new CommentResponse(comment);
            if(thisComment.isRoot()) result.put(thisComment.getComment_id().toString(), thisComment);
            else {
                CommentResponse rootComment = result.get(comment.getRootCommentId().toString());
                if(rootComment == null) continue;

                rootComment.getReply().add(thisComment);
            }
        }

        return result.values().stream().toList();
    }

    public URI addComment(String internalId, String postId, CommentCreateRequest body) {
        Optional<Member> requestMember = this.memberRepository.findByIdAndDeletedAtIsNull(internalId);
        if(requestMember.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Member member = requestMember.get();

        Comment.CommentBuilder commentBuilder = Comment.builder();
        commentBuilder
                .postId(postId).internalUserId(internalId)
                .userId(member.getPublicId()).userName(member.getName())
                .profileImage(member.getProfileImage()).text(body.getText())
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .isRoot(true);

        Badge profileBadge = member.getBadge();
        if(profileBadge != null) {
            Comment.Badge badge = new Comment.Badge();
            badge.setId(profileBadge.getId());
            badge.setName(profileBadge.getName());
            badge.setUrl(profileBadge.getUrl());
            commentBuilder.badge(badge);
        }

        Comment resultComment = this.commentRepository.insert(commentBuilder.build());

        //Increase post comment count
        try {
            Query query = Query.query(Criteria.where("_id").is(postId));
            Update update = new Update().inc("commentCount", 1);
            this.mongoTemplate.updateFirst(query, update, Post.class);
        } catch (Exception e) {
            this.commentRepository.delete(resultComment);
            throw e;
        }

        return URI.create("/comment/" + postId);
    }

    public URI addReply(String internalId, String postId, String commentId, CommentCreateRequest body) {
        Optional<Member> requestMember = this.memberRepository.findByIdAndDeletedAtIsNull(internalId);
        if(requestMember.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Member member = requestMember.get();

        Comment.CommentBuilder commentBuilder = Comment.builder();
        commentBuilder
                .postId(postId).internalUserId(internalId)
                .userId(member.getPublicId()).userName(member.getName())
                .profileImage(member.getProfileImage()).text(body.getText())
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .isRoot(false).rootCommentId(new ObjectId(commentId));

        Badge profileBadge = member.getBadge();
        if(profileBadge != null) {
            Comment.Badge badge = new Comment.Badge();
            badge.setId(profileBadge.getId());
            badge.setName(profileBadge.getName());
            badge.setUrl(profileBadge.getUrl());
            commentBuilder.badge(badge);
        }

        Comment resultComment = this.commentRepository.insert(commentBuilder.build());

        //Increase post comment count
        try {
            Query query = Query.query(Criteria.where("_id").is(postId));
            Update update = new Update().inc("commentCount", 1);
            this.mongoTemplate.updateFirst(query, update, Post.class);
        } catch (Exception e) {
            this.commentRepository.delete(resultComment);
            throw e;
        }

        return URI.create("/comment/" + postId);
    }

//    public deleteComment(String internalId, String commentId) {}
}
