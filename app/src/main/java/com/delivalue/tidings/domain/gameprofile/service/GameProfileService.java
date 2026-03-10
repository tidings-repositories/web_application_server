package com.delivalue.tidings.domain.gameprofile.service;

import com.delivalue.tidings.domain.data.entity.MemberGameProfile;
import com.delivalue.tidings.domain.data.repository.MemberGameProfileRepository;
import com.delivalue.tidings.domain.data.repository.TopicRepository;
import com.delivalue.tidings.domain.gameprofile.dto.GameProfileRequest;
import com.delivalue.tidings.domain.gameprofile.dto.GameProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameProfileService {

    private final MemberGameProfileRepository memberGameProfileRepository;
    private final TopicRepository topicRepository;

    public List<GameProfileResponse> getGameProfiles(String memberId) {
        return memberGameProfileRepository.findAllByMemberId(memberId).stream()
                .map(GameProfileResponse::new)
                .toList();
    }

    public GameProfileResponse addGameProfile(String memberId, GameProfileRequest request) {
        if (!topicRepository.existsById(request.getTopicId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 토픽입니다.");
        }

        MemberGameProfile profile = MemberGameProfile.builder()
                .memberId(memberId)
                .topicId(request.getTopicId())
                .playType(request.getPlayType() != null ? request.getPlayType() : "MAIN")
                .skillLevel(request.getSkillLevel())
                .platform(request.getPlatform())
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        MemberGameProfile saved = memberGameProfileRepository.save(profile);
        return new GameProfileResponse(saved);
    }

    public void removeGameProfile(String memberId, Long profileId) {
        MemberGameProfile profile = memberGameProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!profile.getMemberId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        memberGameProfileRepository.delete(profile);
    }
}
