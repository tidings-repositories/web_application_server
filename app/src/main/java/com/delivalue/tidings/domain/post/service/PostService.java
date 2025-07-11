package com.delivalue.tidings.domain.post.service;

import com.delivalue.tidings.domain.data.entity.*;
import com.delivalue.tidings.domain.data.repository.*;
import com.delivalue.tidings.domain.post.dto.PostCreateRequest;
import com.delivalue.tidings.domain.post.dto.PostResponse;
import com.mongodb.DuplicateKeyException;
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
import java.util.Date;
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
    private final ReportRepository reportRepository;
    private final FollowRepository followRepository;
    private final FeedRepository feedRepository;

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

    public List<PostResponse> getFeedPostByCursor(String internalId, String cursorId, LocalDateTime cursorTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where("internalUserId").is(internalId));

        if(cursorId != null && cursorTime != null) {
            query.addCriteria(
                new Criteria().orOperator(
                    Criteria.where("createdAt").lt(cursorTime),
                    new Criteria().andOperator(
                            Criteria.where("createdAt").is(cursorTime),
                            Criteria.where("postId").lt(cursorId)
                    )
                )
            );
        }
        query.with(Sort.by(Sort.Direction.DESC, "createdAt", "postId"));
        query.limit(15);

        List<String> feedList = this.mongoTemplate.find(query, Feed.class).stream().map(Feed::getPostId).toList();
        List<Post> feedPostList = this.postRepository.findByIdInAndDeletedAtIsNull(feedList);

        return feedPostList.stream().map(PostResponse::new).collect(Collectors.toList());
    }

    public List<PostResponse> getUserPostByCursor(String userId, LocalDateTime cursorTime) {
        Member member = this.memberRepository.findByPublicId(userId);
        if(member == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if(member.getDeletedAt() != null) throw new ResponseStatusException(HttpStatus.GONE);

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

        try {
            this.postRepository.insert(request.toEntity());
        } catch (DuplicateKeyException e) {
            String newID = UUID.randomUUID().toString();
            request.setId(newID);
            this.postRepository.insert(request.toEntity());
        }

        //TODO: 이후 Worker server로 기능 이동
        try{
            int expirationTime = 3600 * 24 * 7;
            Date expiredAt = new Date(System.currentTimeMillis() + expirationTime * 1000L);

            List<Member> followers = this.followRepository.findFollowerMemberById(member.getId());
            List<Feed> feeds = followers.stream().map(
                    thisFollower -> Feed.builder()
                            .internalUserId(thisFollower.getId())
                            .postId(request.getId())
                            .createdAt(request.getCreatedAt())
                            .expiredAt(expiredAt).build()
            ).toList();

            this.feedRepository.insert(feeds);

            return URI.create("/post/" + request.getId());
        } catch (Exception e) {
            return URI.create("/post/" + request.getId());
        }
    }

    public void deletePost(String internalId, String postId) {
        Optional<Post> findPost = this.postRepository.findById(postId);
        if(findPost.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Post post = findPost.get();
        if(!post.getInternalUserId().equals(internalId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        Query query = Query.query(Criteria.where("_id").is(postId));
        Update update = new Update().set("deletedAt", now).set("scrapCount", 0);
        this.mongoTemplate.updateFirst(query, update, Post.class);

        if(post.isOrigin() && post.getScrapCount() > 0) {
            //Spread deleted 설정
            Query deleteSpreadQuery = Query.query(Criteria.where("originalPostId").is(post.getId()));
            this.mongoTemplate.updateMulti(deleteSpreadQuery, update, Post.class);
        } else if(!post.isOrigin()) {
            try {
                Query scrapDecreaseQuery = Query.query(Criteria.where("_id").is(post.getOriginalPostId()));
                Update scrapDecreaseUpdate = new Update().inc("scrapCount", -1);
                this.mongoTemplate.updateFirst(scrapDecreaseQuery, scrapDecreaseUpdate, Post.class);
            } catch (Exception e) {
                Update compenstateUpdate = new Update().set("deletedAt", null);
                this.mongoTemplate.updateFirst(query, compenstateUpdate, Post.class);
                throw e;
            }
        }
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

        this.postLikeRepository.insert(newLikeEntity);

        try {
            //Increase Post document like count
            Query query = Query.query(Criteria.where("_id").is(post.getId()));
            Update update = new Update().inc("likeCount", 1);
            this.mongoTemplate.updateFirst(query, update, Post.class);
        } catch (Exception e) {
            this.postLikeRepository.delete(newLikeEntity);
            throw e;
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
            throw e;
        }
    }

    public List<PostResponse> getUserLikePostByCursor(String userId, LocalDateTime cursorTime, String cursorId) {
        Member member = this.memberRepository.findByPublicId(userId);
        if(member == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if(member.getDeletedAt() != null) throw new ResponseStatusException(HttpStatus.GONE);

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

    public URI scrapPost(String internalId, String postId) {
        //멤버 존재하는지 확인
        Optional<Member> requestMember = this.memberRepository.findById(internalId);
        if(requestMember.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Member member = requestMember.get();

        //포스트가 존재하는지 확인
        Optional<Post> targetPost = this.postRepository.findByIdAndDeletedAtIsNull(postId);
        if(targetPost.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Post post = targetPost.get();

        //동일한 포스트를 이미 스크랩했는지 확인
        Optional<Post> existScrap = this.postRepository.findByOriginalPostIdAndInternalUserId(
                post.isOrigin() ? postId : post.getOriginalPostId(),
                internalId
        );
        if(existScrap.isPresent()) {
            Post presentScrap = existScrap.get();
            if(presentScrap.getDeletedAt() == null)
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);

            //만약 스크랩 이력이 있지만, 삭제했었다면 삭제 취소
            presentScrap.setDeletedAt(null);
            this.postRepository.save(presentScrap);

            try {
                Query query = Query.query(Criteria.where("_id").is(presentScrap.getOriginalPostId()));
                Update update = new Update().inc("scrapCount", 1);
                this.mongoTemplate.updateFirst(query, update, Post.class);
            } catch (Exception e) {
                presentScrap.setDeletedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
                this.postRepository.save(presentScrap);
                throw e;
            }

            return URI.create("/post/" + presentScrap.getId());
        }

        //포스트 주인이 자신의 포스트를 스크랩하려는지 확인
        if(internalId.equals(post.getInternalUserId()) || member.getPublicId().equals(post.getOriginalUserId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        //스크랩 데이터 초기화
        if(post.isOrigin()) {
            post.setOriginalPostId(post.getId());
            post.setOriginalUserId(post.getUserId());
            post.setOrigin(false);
        }
        post.setInternalUserId(internalId);
        post.setUserId(member.getPublicId());
        post.setId(UUID.randomUUID().toString());
        post.setCommentCount(0);

        //저장
        try {
            this.postRepository.insert(post);
        } catch (DuplicateKeyException e) {
            String newID = UUID.randomUUID().toString();
            post.setId(newID);
            this.postRepository.insert(post);
        }

        try {
            //Increase Post document scrap count
            Query query = Query.query(Criteria.where("_id").is(post.getOriginalPostId()));
            Update update = new Update().inc("scrapCount", 1);
            this.mongoTemplate.updateFirst(query, update, Post.class);
        } catch (Exception e) {
            this.postRepository.delete(post);
            throw e;
        }

        //TODO: 이후 Worker server로 기능 이동
        //스크랩한 포스트도 피드에 반영
        try{
            int expirationTime = 3600 * 24 * 7;
            Date expiredAt = new Date(System.currentTimeMillis() + expirationTime * 1000L);

            List<Member> followers = this.followRepository.findFollowerMemberById(member.getId());
            List<Feed> feeds = followers.stream().map(
                    thisFollower -> Feed.builder()
                            .internalUserId(thisFollower.getId())
                            .postId(post.getId())
                            .createdAt(post.getCreatedAt())
                            .expiredAt(expiredAt).build()
            ).toList();

            this.feedRepository.insert(feeds);

            return URI.create("/post/" + post.getId());
        } catch (Exception e) {
            return URI.create("/post/" + post.getId());
        }
    }

    public void reportPost(String internalId, String postId) {
        Report report = Report.builder()
                .targetType("post")
                .targetId(postId)
                .reportUser(internalId)
                .reportAt(LocalDateTime.now(ZoneId.of("Asia/Seoul"))).build();

        this.reportRepository.insert(report);
    }
}
