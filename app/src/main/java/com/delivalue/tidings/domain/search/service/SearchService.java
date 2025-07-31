package com.delivalue.tidings.domain.search.service;

import com.delivalue.tidings.domain.data.entity.Badge;
import com.delivalue.tidings.domain.data.entity.MemberSearch;
import com.delivalue.tidings.domain.data.entity.PostSearch;
import com.delivalue.tidings.domain.data.repository.BadgeRepository;
import com.delivalue.tidings.domain.data.repository.MemberSearchRepository;
import com.delivalue.tidings.domain.data.repository.PostSearchRepository;
import com.delivalue.tidings.domain.post.dto.PostResponse;
import com.delivalue.tidings.domain.profile.dto.BadgeDto;
import com.delivalue.tidings.domain.profile.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final PostSearchRepository postSearchRepository;
    private final MemberSearchRepository memberSearchRepository;
    private final BadgeRepository badgeRepository;

    private final Pageable pageable = PageRequest.of(0, 30);

    @Transactional(readOnly = true)
    public List<ProfileResponse> getProfileBySearchKeyword(String keyword) {
        List<MemberSearch> searchResult = this.memberSearchRepository.searchMembers(keyword, pageable);


        return searchResult.stream().map(result -> {
            Integer badgeId = result.getBadge();

            if(badgeId != null) {
                Optional<Badge> targetBadge = this.badgeRepository.findById(badgeId);

                BadgeDto badge = targetBadge.map(BadgeDto::new).orElse(null);
                return new ProfileResponse(result, badge);
            }

            return new ProfileResponse(result, null);
        }).toList();
    }

    public List<PostResponse> getPostBySearchKeyword(String keyword) {
        List<PostSearch> searchResult = this.postSearchRepository.searchPosts(keyword, pageable);

        return searchResult.stream().map(PostResponse::new).toList();
    }
}
