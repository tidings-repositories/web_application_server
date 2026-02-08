package com.delivalue.tidings.domain.auth.service;

import com.delivalue.tidings.common.TokenProvider;
import com.delivalue.tidings.domain.auth.dto.LoginResponse;
import com.delivalue.tidings.domain.auth.dto.PublicIdValidateResponse;
import com.delivalue.tidings.domain.auth.dto.RegisterRequest;
import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.repository.FollowRepository;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private FollowRepository followRepository;

	@Mock
	private TokenProvider tokenProvider;

	@InjectMocks
	private AuthService authService;

	@Nested
	@DisplayName("checkUserExist - 사용자 존재 여부 확인")
	class CheckUserExist {

		@Test
		@DisplayName("존재하고 삭제되지 않은 회원이면 true를 반환한다")
		void existingActiveMember_returnsTrue() {
			Member member = Member.builder().id("google@123").deletedAt(null).build();
			when(memberRepository.findById("google@123")).thenReturn(Optional.of(member));

			boolean result = authService.checkUserExist("google@123");

			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("존재하지만 삭제된 회원이면 false를 반환한다")
		void existingDeletedMember_returnsFalse() {
			Member member = Member.builder().id("google@123").deletedAt(LocalDateTime.now()).build();
			when(memberRepository.findById("google@123")).thenReturn(Optional.of(member));

			boolean result = authService.checkUserExist("google@123");

			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("존재하지 않는 회원이면 false를 반환한다")
		void nonExistingMember_returnsFalse() {
			when(memberRepository.findById("google@999")).thenReturn(Optional.empty());

			boolean result = authService.checkUserExist("google@999");

			assertThat(result).isFalse();
		}
	}

	@Nested
	@DisplayName("checkPublicIdUsable - Public ID 사용 가능 여부 확인")
	class CheckPublicIdUsable {

		@Test
		@DisplayName("유효한 형식이고 미사용 ID이면 사용 가능하다")
		void validAndAvailable_returnsEnable() {
			when(memberRepository.findByPublicId("newuser")).thenReturn(null);

			PublicIdValidateResponse result = authService.checkPublicIdUsable("newuser");

			assertThat(result.isResult()).isTrue();
			assertThat(result.getStatusMessage()).isEqualTo("enableId");
		}

		@Test
		@DisplayName("이미 사용 중인 ID이면 사용 불가하다")
		void alreadyTaken_returnsAlreadyTaken() {
			Member existingMember = Member.builder().id("google@123").publicId("takenid").build();
			when(memberRepository.findByPublicId("takenid")).thenReturn(existingMember);

			PublicIdValidateResponse result = authService.checkPublicIdUsable("takenid");

			assertThat(result.isResult()).isFalse();
			assertThat(result.getStatusMessage()).isEqualTo("alreadyTaken");
		}

		@Test
		@DisplayName("너무 짧은 ID이면 형식 오류를 반환한다")
		void tooShort_returnsOverRange() {
			PublicIdValidateResponse result = authService.checkPublicIdUsable("ab");

			assertThat(result.isResult()).isFalse();
			assertThat(result.getStatusMessage()).isEqualTo("overRangeString");
			verify(memberRepository, never()).findByPublicId(anyString());
		}

		@Test
		@DisplayName("금지어가 포함된 ID이면 사용 불가하다")
		void forbiddenWord_returnsDisableId() {
			PublicIdValidateResponse result = authService.checkPublicIdUsable("admin");

			assertThat(result.isResult()).isFalse();
			assertThat(result.getStatusMessage()).isEqualTo("disableId");
		}
	}

	@Nested
	@DisplayName("deleteMember - 회원 탈퇴")
	class DeleteMember {

		@Test
		@DisplayName("존재하는 활성 회원을 삭제한다")
		void existingActiveMember_setsDeletedAt() {
			Member member = Member.builder().id("google@123").deletedAt(null).build();
			when(memberRepository.findById("google@123")).thenReturn(Optional.of(member));

			authService.deleteMember("google@123");

			assertThat(member.getDeletedAt()).isNotNull();
			verify(memberRepository).save(member);
		}

		@Test
		@DisplayName("존재하지 않는 회원 삭제 시 NOT_FOUND 예외를 발생시킨다")
		void nonExistingMember_throwsNotFound() {
			when(memberRepository.findById("google@999")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> authService.deleteMember("google@999"))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> {
						ResponseStatusException rse = (ResponseStatusException) ex;
						assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
					});
		}

		@Test
		@DisplayName("이미 삭제된 회원 삭제 시 BAD_REQUEST 예외를 발생시킨다")
		void alreadyDeletedMember_throwsBadRequest() {
			Member member = Member.builder()
					.id("google@123")
					.deletedAt(LocalDateTime.now())
					.build();
			when(memberRepository.findById("google@123")).thenReturn(Optional.of(member));

			assertThatThrownBy(() -> authService.deleteMember("google@123"))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> {
						ResponseStatusException rse = (ResponseStatusException) ex;
						assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
					});
		}
	}

	@Nested
	@DisplayName("registerMember - 회원 가입")
	class RegisterMember {

		@Test
		@DisplayName("신규 회원 등록 시 토큰을 발급하고 login 결과를 반환한다")
		void newMember_registersAndReturnsTokens() {
			RegisterRequest registerRequest = new RegisterRequest(
					"google@123", "newuser", "Test User", "test@example.com"
			);
			when(memberRepository.findById("google@123")).thenReturn(Optional.empty());
			when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
			when(tokenProvider.generateJWT("google@123", "REFRESH")).thenReturn("refresh.token");
			when(tokenProvider.generateJWT("google@123", "ACCESS")).thenReturn("access.token");

			LoginResponse result = authService.registerMember(registerRequest);

			assertThat(result.getResult()).isEqualTo("login");
			assertThat(result.getRefreshToken()).isEqualTo("refresh.token");
			assertThat(result.getAccessToken()).isEqualTo("access.token");
			verify(memberRepository).save(any(Member.class));
			verify(followRepository).save(any());
		}

		@Test
		@DisplayName("이전에 삭제된 회원이 재가입하면 deletedAt을 null로 복원한다")
		void previouslyDeletedMember_restoresAccount() {
			Member existingMember = Member.builder()
					.id("google@123")
					.deletedAt(LocalDateTime.now())
					.build();
			RegisterRequest registerRequest = new RegisterRequest(
					"google@123", "returnuser", "Test", "test@example.com"
			);
			when(memberRepository.findById("google@123")).thenReturn(Optional.of(existingMember));
			when(memberRepository.save(existingMember)).thenReturn(existingMember);
			when(tokenProvider.generateJWT("google@123", "REFRESH")).thenReturn("refresh.token");
			when(tokenProvider.generateJWT("google@123", "ACCESS")).thenReturn("access.token");

			LoginResponse result = authService.registerMember(registerRequest);

			assertThat(result.getResult()).isEqualTo("login");
			assertThat(existingMember.getDeletedAt()).isNull();
			verify(followRepository, never()).save(any());
		}

		@Test
		@DisplayName("등록 중 예외 발생 시 예외가 전파된다")
		void exceptionDuringRegister_throwsException() {
			RegisterRequest registerRequest = new RegisterRequest(
					"google@123", "erroruser", "Test", "test@example.com"
			);
			when(memberRepository.findById("google@123")).thenReturn(Optional.empty());
			when(memberRepository.save(any(Member.class))).thenThrow(new RuntimeException("DB error"));

			assertThatThrownBy(() -> authService.registerMember(registerRequest))
					.isInstanceOf(RuntimeException.class);
		}
	}
}
