package com.delivalue.tidings.domain.gameprofile.dto;

import com.delivalue.tidings.domain.data.entity.MemberGameProfile;
import lombok.Getter;

@Getter
public class GameProfileResponse {
    private final Long id;
    private final Long topic_id;
    private final String play_type;
    private final String skill_level;
    private final String platform;

    public GameProfileResponse(MemberGameProfile profile) {
        this.id = profile.getId();
        this.topic_id = profile.getTopicId();
        this.play_type = profile.getPlayType();
        this.skill_level = profile.getSkillLevel();
        this.platform = profile.getPlatform();
    }
}
