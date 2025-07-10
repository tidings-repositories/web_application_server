package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "feeds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feed {

    @Id
    private ObjectId id;

    private String internalUserId;
    private String postId;
    private LocalDateTime createdAt;
    private Date expiredAt;
}
