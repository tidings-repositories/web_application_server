package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Badge {
    @Id
    private int id;

    private String name;

    private String url;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
