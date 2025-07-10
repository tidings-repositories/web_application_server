package com.delivalue.tidings.domain.profile.dto;

import com.delivalue.tidings.domain.data.entity.Badge;
import lombok.Getter;

@Getter
public class BadgeDto {
    private final Integer id;
    private final String name;
    private final String url;

    public BadgeDto(Badge badge) {
        this.id = badge.getId();
        this.name = badge.getName();
        this.url = badge.getUrl();
    }
}
