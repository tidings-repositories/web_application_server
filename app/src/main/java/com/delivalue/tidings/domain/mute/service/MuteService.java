package com.delivalue.tidings.domain.mute.service;

import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.entity.MemberMute;
import com.delivalue.tidings.domain.data.entity.embed.MemberRelationId;
import com.delivalue.tidings.domain.data.repository.MemberMuteRepository;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import com.delivalue.tidings.domain.profile.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MuteService {

    private final MemberRepository memberRepository;
    private final MemberMuteRepository memberMuteRepository;

    public void muteMember(String muterId, String targetPublicId) {
        Member target = memberRepository.findByPublicId(targetPublicId);
        if (target == null || target.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        MemberRelationId id = new MemberRelationId(muterId, target.getId());
        if (memberMuteRepository.existsByIdBlockerIdAndIdTargetId(muterId, target.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        MemberMute mute = MemberMute.builder()
                .id(id)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        memberMuteRepository.save(mute);
    }

    public void unmuteMember(String muterId, String targetPublicId) {
        Member target = memberRepository.findByPublicId(targetPublicId);
        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        MemberRelationId id = new MemberRelationId(muterId, target.getId());
        if (!memberMuteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        memberMuteRepository.deleteById(id);
    }

    public List<ProfileResponse> getMuteList(String muterId) {
        List<MemberMute> mutes = memberMuteRepository.findAllByIdBlockerId(muterId);
        List<String> targetIds = mutes.stream()
                .map(m -> m.getId().getTargetId())
                .toList();
        Map<String, Member> memberMap = memberRepository.findAllById(targetIds).stream()
                .collect(Collectors.toMap(Member::getId, m -> m));
        return mutes.stream()
                .map(m -> memberMap.get(m.getId().getTargetId()))
                .filter(Objects::nonNull)
                .map(ProfileResponse::new)
                .toList();
    }
}
