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

	// 기능 31: 게이밍 SNS 특화 필드
	private String postSubType; // TEXT / CLIP / SCREENSHOT / ACHIEVEMENT / LFG / GUIDE / TOURNAMENT_RECAP
	private Long gameId;        // Topic.id 참조 (GAME_TITLE)
	private List<String> gameGenres;    // MOBA / FPS / RPG 등
	private List<String> gamePlatforms; // PC / MOBILE / PS5 등
	private String gameVersion;         // 게임 버전/패치 (예: 14.5)

	// 기능 46: 대화 스레딩
	private String inReplyToPostId; // 답글 대상 포스트 ID

	@Getter
	@NoArgsConstructor
	public static class PostMediaItem {
		@NotBlank
		private String type;
		@NotBlank
		private String url;
	}
}
