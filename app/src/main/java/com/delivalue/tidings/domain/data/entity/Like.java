package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {

    @Id
    private ObjectId id;

    private String postId;
    private String likeUserId;
    private LocalDateTime postCreatedAt;
    private LocalDateTime likeAt;
}
