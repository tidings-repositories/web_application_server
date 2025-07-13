package com.delivalue.tidings.domain.profile.service;

import com.delivalue.tidings.domain.data.entity.*;
import com.delivalue.tidings.domain.data.repository.MemberBadgeRepository;
import com.delivalue.tidings.domain.data.repository.MemberQueryRepositoryImpl;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import com.delivalue.tidings.domain.profile.dto.BadgeListResponse;
import com.delivalue.tidings.domain.profile.dto.ProfileResponse;
import com.delivalue.tidings.domain.profile.dto.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final MemberRepository memberRepository;
    private final MemberQueryRepositoryImpl memberQueryRepository;
    private final MemberBadgeRepository memberBadgeRepository;
    private final MongoTemplate mongoTemplate;

    public ProfileResponse getProfileById(String internalId) {
        Optional<Member> resultMember = this.memberRepository.findById(internalId);
        if(resultMember.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Member member = resultMember.get();
        if(member.getDeletedAt() != null) throw new ResponseStatusException(HttpStatus.GONE);
        if(member.getBannedAt() != null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, member.getBanReason() + " 사유로 차단된 사용자입니다.");

        return new ProfileResponse(member);
    }

    public ProfileResponse getProfileByPublicId(String publicId) {
        Member member = this.memberRepository.findByPublicId(publicId);
        if(member == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if(member.getDeletedAt() != null || member.getBannedAt() != null) throw new ResponseStatusException(HttpStatus.GONE);

        return new ProfileResponse(member);
    }

    public BadgeListResponse getBadgeListById(String internalId) {
        List<MemberBadge> memberBadgeList = this.memberBadgeRepository.findByMemberId(internalId);
        return new BadgeListResponse(memberBadgeList);
    }

    public void updateProfile(ProfileUpdateRequest profileUpdateRequest) {
        Optional<Member> updateMember = this.memberRepository.findById(profileUpdateRequest.getId());
        if(updateMember.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Member member = updateMember.get();
        if(member.getBannedAt() != null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, member.getBanReason() + " 사유로 차단된 사용자입니다.");

        boolean needSpread = false;
        Update spreadUpdate = new Update();

        if(profileUpdateRequest.getProfileImage() != null) {
            spreadUpdate.set("profileImage", profileUpdateRequest.getProfileImage());
            needSpread = true;
        }
        if(profileUpdateRequest.getUserName() != null) {
            spreadUpdate.set("userName", profileUpdateRequest.getUserName());
            needSpread = true;
        }
        if(profileUpdateRequest.getBadgeId() != null) {
            if (profileUpdateRequest.getBadgeId() == 0) {
                spreadUpdate.set("badge", null);
            } else {
                MemberBadge memberBadge = this.memberBadgeRepository.findByMemberIdAndBadge_Id(profileUpdateRequest.getId(), profileUpdateRequest.getBadgeId());
                if(memberBadge == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

                Map<String, Object> badge = new HashMap<>();
                Badge expectBadge = memberBadge.getBadge();
                badge.put("id", expectBadge.getId());
                badge.put("name", expectBadge.getName());
                badge.put("url", expectBadge.getUrl());

                spreadUpdate.set("badge", badge);
            }
            needSpread = true;
        }

        this.memberQueryRepository.updateMemberProfile(profileUpdateRequest);

        Query findPostInternalUserIdQuery = Query.query(
                new Criteria().andOperator(
                        Criteria.where("internalUserId").is(profileUpdateRequest.getId()),
                        Criteria.where("isOrigin").is(true),
                        Criteria.where("deletedAt").isNull()
                )
        );

        Query findPostOriginUserIdQuery = Query.query(
                new Criteria().andOperator(
                        Criteria.where("originalUserId").is(member.getPublicId()),
                        Criteria.where("isOrigin").is(false)
                )
        );

        Query findCommentInternalUserIdQuery = Query.query(
                new Criteria().andOperator(
                        Criteria.where("internalUserId").is(profileUpdateRequest.getId()),
                        Criteria.where("deletedAt").isNull()
                )
        );

        //TODO: 이후 Worker server로 기능 이동
        if(needSpread) {
            this.mongoTemplate.updateMulti(findPostInternalUserIdQuery, spreadUpdate, Post.class);
            this.mongoTemplate.updateMulti(findPostOriginUserIdQuery, spreadUpdate, Post.class);
            this.mongoTemplate.updateMulti(findCommentInternalUserIdQuery, spreadUpdate, Comment.class);
        }
    }
}
