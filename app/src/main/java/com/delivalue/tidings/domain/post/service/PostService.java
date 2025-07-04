package com.delivalue.tidings.domain.post.service;

import com.delivalue.tidings.domain.data.entity.Badge;
import com.delivalue.tidings.domain.data.entity.Like;
import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.entity.Post;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import com.delivalue.tidings.domain.data.repository.PostLikeRepository;
import com.delivalue.tidings.domain.data.repository.PostRepository;
import com.delivalue.tidings.domain.post.dto.PostCreateRequest;
import com.delivalue.tidings.domain.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final MongoTemplate mongoTemplate;
    private final PostLikeRepository postLikeRepository;

    public PostResponse getPostByPostId(String postId) {
        Optional<Post> result = this.postRepository.findByIdAndDeletedAtIsNull(postId);

        if(result.isPresent()) return new PostResponse(result.get());
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public List<PostResponse> getRecentPostByCursor(String cursorId, LocalDateTime cursorTime) {
        Query query = new Query();

        if(cursorId != null && cursorTime != null) {
            query.addCriteria(
                new Criteria().orOperator(
                    Criteria.where("createdAt").lt(cursorTime),
                    new Criteria().andOperator(
                            Criteria.where("createdAt").is(cursorTime),
                            Criteria.where("_id").lt(cursorId)
                    )
                )
            );
        }

        query.addCriteria(Criteria.where("deletedAt").is(null));

        query.with(Sort.by(Sort.Direction.DESC, "createdAt", "_id"));
        query.limit(15);
        List<Post> results = this.mongoTemplate.find(query, Post.class);
        return results.stream().map(PostResponse::new).collect(Collectors.toList());
    }

    public List<PostResponse> getUserPostByCursor(String userId, LocalDateTime cursorTime) {
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
        List<Post> results = this.mongoTemplate.find(query, Post.class);
        return results.stream().map(PostResponse::new).collect(Collectors.toList());
    }

    public URI createPost(PostCreateRequest request) {
        Optional<Member> requestMember = this.memberRepository.findById(request.getInternalUserId());
        if(requestMember.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Member member = requestMember.get();
        request.setUserId(member.getPublicId());
        request.setUserName(member.getName());
        request.setProfileImage(member.getProfileImage());

        Badge profileBadge = member.getBadge();
        if(profileBadge != null) {
            Post.Badge postBadge = new Post.Badge();
            postBadge.setId(profileBadge.getId());
            postBadge.setName(profileBadge.getName());
            postBadge.setUrl(profileBadge.getUrl());
            request.setBadge(postBadge);
        }

        boolean exists = this.postRepository.existsById(request.getId());
        if(!exists) {
            this.postRepository.save(request.toEntity());
            return URI.create("/post/" + request.getId());
        } else {
            while(true) {
                String newID = UUID.randomUUID().toString();
                boolean assignment = this.postRepository.existsById(newID);
                if(!assignment) continue;

                request.setId(newID);
                this.postRepository.save(request.toEntity());
                return URI.create("/post/" + newID);
            }
        }
    }

    public void deletePost(String internalId, String postId) {
        Optional<Post> findPost = this.postRepository.findById(postId);
        if(findPost.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Post post = findPost.get();
        if(!post.getInternalUserId().equals(internalId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        post.setDeletedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        this.postRepository.save(post);
    }

    public void likePost(String internalId, String postId) {
        //Add Like document
        Optional<Member> likeMember = this.memberRepository.findById(internalId);
        if(likeMember.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Member member = likeMember.get();

        Optional<Post> likePost = this.postRepository.findById(postId);
        if(likePost.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Post post = likePost.get();
        if(post.getInternalUserId().equals(internalId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        Optional<Like> like = this.postLikeRepository.findByLikeUserIdAndPostId(member.getPublicId(), postId);
        if(like.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Like newLikeEntity = Like.builder()
                .likeUserId(member.getPublicId())
                .postId(postId)
                .postCreatedAt(post.getCreatedAt())
                .likeAt(LocalDateTime.now(ZoneId.of("Asia/Seoul"))).build();

        this.postLikeRepository.save(newLikeEntity);

        try {
            //Increase Post document like count
            Query query = Query.query(Criteria.where("_id").is(post.getId()));
            Update update = new Update().inc("likeCount", 1);
            this.mongoTemplate.updateFirst(query, update, Post.class);
        } catch (Exception e) {
            this.postLikeRepository.delete(newLikeEntity);
        }
    }

    public void unlikePost(String internalId, String postId) {
        //Remove Like document
        Optional<Member> unlikeMember = this.memberRepository.findById(internalId);
        if(unlikeMember.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Member member = unlikeMember.get();

        Optional<Like> like = this.postLikeRepository.findByLikeUserIdAndPostId(member.getPublicId(), postId);
        if(like.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Like likeEntity = like.get();
        this.postLikeRepository.delete(likeEntity);

        try {
            //Decrease Post document like count
            Query query = Query.query(Criteria.where("_id").is(likeEntity.getPostId()));
            Update update = new Update().inc("likeCount", -1);
            this.mongoTemplate.updateFirst(query, update, Post.class);
        } catch (Exception e) {
            this.postLikeRepository.save(likeEntity);
        }
    }

    public List<PostResponse> getUserLikePostByCursor(String userId, LocalDateTime cursorTime, String cursorId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("likeUserId").is(userId));

        if(cursorId != null) {
            query.addCriteria(
                    new Criteria().orOperator(
                            Criteria.where("postCreatedAt").lt(cursorTime),
                            new Criteria().andOperator(
                                    Criteria.where("postCreatedAt").is(cursorTime),
                                    Criteria.where("postId").lt(cursorId)
                            )
                    )
            );
        }

        query.with(Sort.by(Sort.Direction.DESC, "postCreatedAt"));
        query.limit(15);

        List<Like> likeList = this.mongoTemplate.find(query, Like.class);
        List<String> postIdList = likeList.stream().map(Like::getPostId).toList();
        List<Post> likePostList = this.postRepository.findByIdInAndDeletedAtIsNull(postIdList);

        return likePostList.stream().map(PostResponse::new).collect(Collectors.toList());
    }
}
