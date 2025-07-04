package com.delivalue.tidings.domain.post.service;

import com.delivalue.tidings.domain.data.entity.Badge;
import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.entity.Post;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import com.delivalue.tidings.domain.data.repository.PostRepository;
import com.delivalue.tidings.domain.post.dto.PostCreateRequest;
import com.delivalue.tidings.domain.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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
                            new Criteria().andOperator(
                                    Criteria.where("createdAt").lt(cursorTime),
                                    Criteria.where("deletedAt").is(null)
                            ),
                            new Criteria().andOperator(
                                    Criteria.where("createdAt").is(cursorTime),
                                    Criteria.where("_id").lt(cursorId),
                                    Criteria.where("deletedAt").is(null)
                            )
                    )
            );
        }

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

    public void createPost(PostCreateRequest request) {
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
        if(!exists) this.postRepository.save(request.toEntity());
        else {
            while(true) {
                String newID = UUID.randomUUID().toString();
                boolean assignment = this.postRepository.existsById(newID);
                if(!assignment) continue;

                request.setId(newID);
                this.postRepository.save(request.toEntity());
                return;
            }
        }
    }
}
