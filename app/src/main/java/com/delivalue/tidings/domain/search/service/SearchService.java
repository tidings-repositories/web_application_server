package com.delivalue.tidings.domain.search.service;

import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import com.delivalue.tidings.domain.profile.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final MemberRepository memberRepository;

    public List<ProfileResponse> getProfileBySearchKeyword(String keyword) {
        List<Member> searchResult = this.memberRepository.searchByKeyword(keyword);

        return searchResult.stream().map(ProfileResponse::new).collect(Collectors.toList());
    }
}
