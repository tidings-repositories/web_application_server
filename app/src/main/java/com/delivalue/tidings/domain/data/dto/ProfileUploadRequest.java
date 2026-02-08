package com.delivalue.tidings.domain.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUploadRequest {
	@NotBlank
	@JsonProperty("content-type")
	private String contentType;
}
