package com.delivalue.tidings.domain.post.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostContentRequest {
	@NotNull
	private String text;
	private List<@Valid PostMediaItem> media;
	private List<String> tag;

	@Getter
	@NoArgsConstructor
	public static class PostMediaItem {
		@NotBlank
		private String type;
		@NotBlank
		private String url;
	}
}
