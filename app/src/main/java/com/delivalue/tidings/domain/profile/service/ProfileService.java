package com.delivalue.tidings.domain.profile.service;

import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.entity.MemberBadge;
import com.delivalue.tidings.domain.data.repository.MemberBadgeRepository;
import com.delivalue.tidings.domain.data.repository.MemberQueryRepositoryImpl;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import com.delivalue.tidings.domain.profile.dto.ProfileResponse;
import com.delivalue.tidings.domain.profile.dto.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final MemberRepository memberRepository;
    private final MemberQueryRepositoryImpl memberQueryRepository;
    private final MemberBadgeRepository memberBadgeRepository;

    public ProfileResponse getProfileById(String internalId) {
        Optional<Member> member = this.memberRepository.findById(internalId);

        return member.isPresent() ? new ProfileResponse(member.get()) : null;
    }

    public ProfileResponse getProfileByPublicId(String publicId) {
        Member member = this.memberRepository.findByPublicId(publicId);

        return member != null ? new ProfileResponse(member) : null;
    }

    public void updateProfile(ProfileUpdateRequest profileUpdateRequest) {
        if(profileUpdateRequest.getBadgeId() != null) {
            MemberBadge memberBadge = this.memberBadgeRepository.findByMemberIdAndBadge_Id(profileUpdateRequest.getId(), profileUpdateRequest.getBadgeId());
            if(memberBadge == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        this.memberQueryRepository.updateMemberProfile(profileUpdateRequest);
    }
}
