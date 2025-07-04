package com.delivalue.tidings.domain.follow.service;

import com.delivalue.tidings.domain.data.entity.Follow;
import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.entity.embed.FollowId;
import com.delivalue.tidings.domain.data.repository.FollowRepository;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import com.delivalue.tidings.domain.profile.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addFollowUser(String followerId, String followingUserPublicId) {
        Member followingUser = this.memberRepository.findByPublicId(followingUserPublicId);
        if(followingUser == null || followerId.equals(followingUser.getId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        FollowId id = new FollowId(followingUser.getId(), followerId);
        Follow followEntity = Follow.builder().id(id).createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul"))).build();
        boolean exists = this.followRepository.existsById(id);
        if(!exists) {
            this.followRepository.save(followEntity);

            this.memberRepository.increaseFollowerCount(followingUser.getId());
            this.memberRepository.increaseFollowingCount(followerId);
        }
    }

    @Transactional
    public void removeFollowUser(String followerId, String followingUserPublicId) {
        Member followingUser = this.memberRepository.findByPublicId(followingUserPublicId);
        if(followingUser == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        FollowId id = new FollowId(followingUser.getId(), followerId);
        boolean exists = this.followRepository.existsById(id);
        if(exists) {
            this.followRepository.deleteById(id);

            this.memberRepository.decreaseFollowerCount(followingUser.getId());
            this.memberRepository.decreaseFollowingCount(followerId);
        }
    }

    public List<ProfileResponse> getFollowingList(String publicId) {
        Member member = this.memberRepository.findByPublicId(publicId);
        if(member == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        List<Member> followingMemberEntityList = this.followRepository.findFollowingMemberById(member.getId());
        return followingMemberEntityList.stream().map(ProfileResponse::new).collect(Collectors.toList());
    }

    public List<ProfileResponse> getFollowerList(String publicId) {
        Member member = this.memberRepository.findByPublicId(publicId);
        if(member == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        List<Member> followerMemberEntityList = this.followRepository.findFollowerMemberById(member.getId());
        return followerMemberEntityList.stream().map(ProfileResponse::new).collect(Collectors.toList());
    }
}
