package com.delivalue.tidings.domain.profile.service;

import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.entity.MemberBadge;
import com.delivalue.tidings.domain.data.repository.MemberBadgeRepository;
import com.delivalue.tidings.domain.data.repository.MemberQueryRepositoryImpl;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import com.delivalue.tidings.domain.profile.dto.BadgeListResponse;
import com.delivalue.tidings.domain.profile.dto.ProfileResponse;
import com.delivalue.tidings.domain.profile.dto.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final MemberRepository memberRepository;
    private final MemberQueryRepositoryImpl memberQueryRepository;
    private final MemberBadgeRepository memberBadgeRepository;

    public ProfileResponse getProfileById(String internalId) {
        Optional<Member> resultMember = this.memberRepository.findById(internalId);
        if(resultMember.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Member member = resultMember.get();
        if(member.getDeletedAt() != null) throw new ResponseStatusException(HttpStatus.GONE);

        return new ProfileResponse(member);
    }

    public ProfileResponse getProfileByPublicId(String publicId) {
        Member member = this.memberRepository.findByPublicId(publicId);
        if(member == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if(member.getDeletedAt() != null) throw new ResponseStatusException(HttpStatus.GONE);

        return new ProfileResponse(member);
    }

    public BadgeListResponse getBadgeListById(String internalId) {
        List<MemberBadge> memberBadgeList = this.memberBadgeRepository.findByMemberId(internalId);
        return new BadgeListResponse(memberBadgeList);
    }


    public void updateProfile(ProfileUpdateRequest profileUpdateRequest) {
        if(profileUpdateRequest.getBadgeId() != null) {
            MemberBadge memberBadge = this.memberBadgeRepository.findByMemberIdAndBadge_Id(profileUpdateRequest.getId(), profileUpdateRequest.getBadgeId());
            if(memberBadge == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        this.memberQueryRepository.updateMemberProfile(profileUpdateRequest);

        //TODO: 이후 Worker server로 기능 이동
        //TODO: `이름` 또는 `프로필 사진` 또는 `뱃지` 변경 시 자신의 포스트 및 코멘트들 내용도 함께 변경
        // 1. 포스트의 internalId가 자신이면서 isOrigin == true일 때 deletedAt이 null인 경우,
        // 2. 포스트의 isOrigin이 false인데 originPostId가 삭제되지 않았고 originalUserId (publicId)가 자신일 때 (deletedAt 여부 상관 x)
        // 3. 코멘트의 internalId가 자신이면서 deletedAt이 null인 경우,
    }
}
