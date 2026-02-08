package com.delivalue.tidings.domain.post.dto;

import com.delivalue.tidings.domain.data.entity.Post;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostCreateRequestTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private PostContentRequest createPostContentRequest(String text, List<String> tags,
			List<Map<String, String>> media) {
		Map<String, Object> json = new HashMap<>();
		if (text != null) {
			json.put("text", text);
		}
		if (tags != null) {
			json.put("tag", tags);
		}
		if (media != null) {
			json.put("media", media);
		}
		return objectMapper.convertValue(json, PostContentRequest.class);
	}

	private Map<String, String> mediaItem(String type, String url) {
		return Map.of("type", type, "url", url);
	}

	@Nested
	@DisplayName("생성자 - PostContentRequest에서 PostCreateRequest 변환")
	class Constructor {

		@Test
		@DisplayName("텍스트만 있는 게시글을 올바르게 변환한다")
		void textOnly_createsCorrectly() {
			PostContentRequest contentRequest = createPostContentRequest(
					"Hello World", List.of("tag1", "tag2"), null
			);

			PostCreateRequest createRequest = new PostCreateRequest(contentRequest);

			assertThat(createRequest.getId()).isNotNull().isNotEmpty();
			assertThat(createRequest.getContent()).isNotNull();
			assertThat(createRequest.getContent().getText()).isEqualTo("Hello World");
			assertThat(createRequest.getContent().getTag()).containsExactly("tag1", "tag2");
			assertThat(createRequest.getContent().getMedia()).isEmpty();
			assertThat(createRequest.getCreatedAt()).isNotNull();
			assertThat(createRequest.getCreatedAt()).isBefore(LocalDateTime.now().plusMinutes(1));
			assertThat(createRequest.getCommentCount()).isEqualTo(0);
			assertThat(createRequest.getLikeCount()).isEqualTo(0);
			assertThat(createRequest.getScrapCount()).isEqualTo(0);
			assertThat(createRequest.isOrigin()).isTrue();
		}

		@Test
		@DisplayName("미디어가 포함된 게시글에서 CDN URL로 변환한다")
		void withMedia_convertsUrls() {
			PostContentRequest contentRequest = createPostContentRequest(
					"Photo post", null,
					List.of(mediaItem("image", "https://s3.amazonaws.com/bucket/user/image.jpg"))
			);

			PostCreateRequest createRequest = new PostCreateRequest(contentRequest);

			assertThat(createRequest.getContent().getMedia()).hasSize(1);
			Post.PostMedia postMedia = (Post.PostMedia) createRequest.getContent().getMedia().get(0);
			assertThat(postMedia.getType()).isEqualTo("image");
			assertThat(postMedia.getUrl()).startsWith("https://cdn.stellagram.kr/");
			assertThat(postMedia.getUrl()).contains("/bucket/user/image.jpg");
		}

		@Test
		@DisplayName("여러 미디어 아이템을 올바르게 변환한다")
		void multipleMedia_convertsAll() {
			PostContentRequest contentRequest = createPostContentRequest(
					"Multi media", List.of("photo"),
					List.of(
							mediaItem("image", "https://s3.amazonaws.com/bucket/img1.jpg"),
							mediaItem("video", "https://s3.amazonaws.com/bucket/vid1.mp4")
					)
			);

			PostCreateRequest createRequest = new PostCreateRequest(contentRequest);

			assertThat(createRequest.getContent().getMedia()).hasSize(2);
		}

		@Test
		@DisplayName("각 생성마다 고유한 ID가 부여된다")
		void eachCreation_generatesUniqueId() {
			PostContentRequest contentRequest = createPostContentRequest("Post", null, null);

			PostCreateRequest request1 = new PostCreateRequest(contentRequest);
			PostCreateRequest request2 = new PostCreateRequest(contentRequest);

			assertThat(request1.getId()).isNotEqualTo(request2.getId());
		}

		@Test
		@DisplayName("잘못된 URL이면 URI.create에서 예외가 발생한다")
		void invalidMediaUrl_throwsException() {
			PostContentRequest contentRequest = createPostContentRequest(
					"Bad post", null,
					List.of(mediaItem("image", "not a valid url with spaces"))
			);

			assertThatThrownBy(() -> new PostCreateRequest(contentRequest))
					.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Nested
	@DisplayName("toEntity - Post Entity 변환")
	class ToEntity {

		@Test
		@DisplayName("PostCreateRequest를 Post Entity로 올바르게 변환한다")
		void toEntity_convertsCorrectly() {
			PostContentRequest contentRequest = createPostContentRequest("Test post", List.of("tag"), null);
			PostCreateRequest createRequest = new PostCreateRequest(contentRequest);
			createRequest.setInternalUserId("google@123");
			createRequest.setUserId("testuser");
			createRequest.setUserName("Test User");

			Post entity = createRequest.toEntity();

			assertThat(entity.getId()).isEqualTo(createRequest.getId());
			assertThat(entity.getInternalUserId()).isEqualTo("google@123");
			assertThat(entity.getUserId()).isEqualTo("testuser");
			assertThat(entity.getUserName()).isEqualTo("Test User");
			assertThat(entity.getContent().getText()).isEqualTo("Test post");
			assertThat(entity.getCommentCount()).isEqualTo(0);
			assertThat(entity.getLikeCount()).isEqualTo(0);
			assertThat(entity.getScrapCount()).isEqualTo(0);
			assertThat(entity.isOrigin()).isTrue();
		}
	}
}
