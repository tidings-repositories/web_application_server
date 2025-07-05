package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    private ObjectId id;

    private String targetType;
    private String targetId;
    private String reportUser;
    private LocalDateTime reportAt;
}
