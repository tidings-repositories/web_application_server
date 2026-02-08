package com.delivalue.tidings.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterPublicIdRequest {
	@NotBlank
	private String publicId;
}
