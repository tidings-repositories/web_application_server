package com.delivalue.tidings.domain.comment.service;

import com.delivalue.tidings.domain.comment.dto.CommentCreateRequest;
import com.delivalue.tidings.domain.comment.dto.CommentResponse;
import com.delivalue.tidings.domain.data.entity.*;
import com.delivalue.tidings.domain.data.repository.CommentRepository;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import com.delivalue.tidings.domain.data.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final MongoTemplate mongoTemplate;
    private final ReportRepository reportRepository;

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

    public List<CommentResponse> getUserCommentByCursor(String userId, LocalDateTime cursorTime) {
        Member member = this.memberRepository.findByPublicId(userId);
        if(member == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if(member.getDeletedAt() != null || member.getBannedAt() != null) throw new ResponseStatusException(HttpStatus.GONE);

        Query query = new Query();
        query.addCriteria(
                new Criteria().andOperator(
                        Criteria.where("userId").is(userId),
                        Criteria.where("createdAt").lt(cursorTime),
                        Criteria.where("deletedAt").is(null)
                )
        );

        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        query.limit(15);
        List<Comment> results = this.mongoTemplate.find(query, Comment.class);
        return results.stream().map(CommentResponse::new).collect(Collectors.toList());
    }

    public URI addComment(String internalId, String postId, CommentCreateRequest body) {
        Optional<Member> requestMember = this.memberRepository.findByIdAndDeletedAtIsNull(internalId);
        if(requestMember.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Member member = requestMember.get();
        if(member.getBannedAt() != null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, member.getBanReason() + " 사유로 차단된 사용자입니다.");

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
        if(member.getBannedAt() != null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, member.getBanReason() + " 사유로 차단된 사용자입니다.");

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

    public void deleteComment(String internalId, String commentId) {
        ObjectId id = new ObjectId(commentId);
        Optional<Comment> targetComment = this.commentRepository.findById(id);
        if(targetComment.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Comment comment = targetComment.get();
        if(!internalId.equals(comment.getInternalUserId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().set("deletedAt", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        this.mongoTemplate.updateFirst(query, update, Comment.class);

        try {
            Query postQuery = Query.query(Criteria.where("_id").is(comment.getPostId()));
            Update DecreaseCountUpdate = new Update().inc("commentCount", -1);
            this.mongoTemplate.updateFirst(postQuery, DecreaseCountUpdate, Post.class);
        } catch (Exception e) {
            Update compenstateUpdate = new Update().set("deletedAt", null);
            this.mongoTemplate.updateFirst(query, compenstateUpdate, Comment.class);
            throw e;
        }
    }

    public void reportComment(String internalId, String commentId) {
        Report report = Report.builder()
                .targetType("comment")
                .targetId(commentId)
                .reportUser(internalId)
                .reportAt(LocalDateTime.now(ZoneId.of("Asia/Seoul"))).build();

        this.reportRepository.insert(report);
    }
}
