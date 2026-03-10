package com.delivalue.tidings.domain.block.service;

import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.entity.MemberBlock;
import com.delivalue.tidings.domain.data.entity.embed.MemberRelationId;
import com.delivalue.tidings.domain.data.repository.MemberBlockRepository;
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
public class BlockService {

    private final MemberRepository memberRepository;
    private final MemberBlockRepository memberBlockRepository;

    public void blockMember(String blockerId, String targetPublicId) {
        Member target = memberRepository.findByPublicId(targetPublicId);
        if (target == null || target.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        MemberRelationId id = new MemberRelationId(blockerId, target.getId());
        if (memberBlockRepository.existsByIdBlockerIdAndIdTargetId(blockerId, target.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        MemberBlock block = MemberBlock.builder()
                .id(id)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        memberBlockRepository.save(block);
    }

    public void unblockMember(String blockerId, String targetPublicId) {
        Member target = memberRepository.findByPublicId(targetPublicId);
        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        MemberRelationId id = new MemberRelationId(blockerId, target.getId());
        if (!memberBlockRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        memberBlockRepository.deleteById(id);
    }

    public List<ProfileResponse> getBlockList(String blockerId) {
        List<MemberBlock> blocks = memberBlockRepository.findAllByIdBlockerId(blockerId);
        List<String> targetIds = blocks.stream()
                .map(b -> b.getId().getTargetId())
                .toList();
        Map<String, Member> memberMap = memberRepository.findAllById(targetIds).stream()
                .collect(Collectors.toMap(Member::getId, m -> m));
        return blocks.stream()
                .map(b -> memberMap.get(b.getId().getTargetId()))
                .filter(Objects::nonNull)
                .map(ProfileResponse::new)
                .toList();
    }
}
