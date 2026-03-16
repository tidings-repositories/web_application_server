package com.delivalue.tidings.domain.topic.dto;

import com.delivalue.tidings.domain.data.entity.Topic;
import lombok.Getter;

@Getter
public class TopicResponse {
    private final Long id;
    private final String name;
    private final String display_name;
    private final String category;
    private final Long parent_topic_id;
    private final String language_code;

    public TopicResponse(Topic topic) {
        this.id = topic.getId();
        this.name = topic.getName();
        this.display_name = topic.getDisplayName();
        this.category = topic.getCategory();
        this.parent_topic_id = topic.getParentTopicId();
        this.language_code = topic.getLanguageCode();
    }
}
