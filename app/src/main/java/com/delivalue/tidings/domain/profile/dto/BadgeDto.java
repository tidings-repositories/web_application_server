package com.delivalue.tidings.domain.profile.dto;

import com.delivalue.tidings.domain.data.entity.Badge;
import lombok.Getter;

@Getter
public class BadgeDto {
    private final String name;
    private final String url;

    public BadgeDto(Badge badge) {
        this.name = badge.getName();
        this.url = badge.getUrl();
    }
}
