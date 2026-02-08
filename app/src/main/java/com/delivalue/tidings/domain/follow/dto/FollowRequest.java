package com.delivalue.tidings.domain.follow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowRequest {
	@NotBlank
	private String follow;
}
