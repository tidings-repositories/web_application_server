package com.delivalue.tidings.domain.gameprofile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameProfileRequest {
    @NotNull
    @JsonProperty("topic_id")
    private Long topicId;

    @JsonProperty("play_type")
    private String playType; // MAIN / SUB / PAST

    @JsonProperty("skill_level")
    private String skillLevel; // BEGINNER / INTERMEDIATE / ADVANCED / EXPERT

    private String platform; // PC / MOBILE / PS5 등
}
