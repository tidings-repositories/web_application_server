package com.delivalue.tidings.domain.topic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostTopicRequest {
    @NotNull
    private Long topicId;
}
