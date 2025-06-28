package com.delivalue.tidings.domain.profile.dto;

import com.delivalue.tidings.domain.data.entity.MemberBadge;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BadgeListResponse {
    private final List<BadgeDto> badgeList;

    public BadgeListResponse(List<MemberBadge> memberBadgeList) {
        this.badgeList = memberBadgeList.stream().map(e -> new BadgeDto(e.getBadge())).collect(Collectors.toList());
    }
}
