package com.delivalue.tidings.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
public class CursorRequest {
	private String postId;
	private OffsetDateTime createdAt;
}
