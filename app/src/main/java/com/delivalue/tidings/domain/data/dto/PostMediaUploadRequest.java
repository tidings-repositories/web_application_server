package com.delivalue.tidings.domain.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostMediaUploadRequest {
	@NotEmpty
	@JsonProperty("content-types")
	private List<@NotBlank String> contentTypes;
}
