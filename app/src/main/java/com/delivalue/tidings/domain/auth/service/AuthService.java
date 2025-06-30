package com.delivalue.tidings.domain.auth.service;

import com.delivalue.tidings.common.ForbiddenWordFilter;
import com.delivalue.tidings.common.TokenProvider;
import com.delivalue.tidings.domain.auth.dto.LoginResponse;
import com.delivalue.tidings.domain.auth.dto.PublicIdValidateResponse;
import com.delivalue.tidings.domain.auth.dto.RegisterRequest;
import com.delivalue.tidings.domain.data.entity.Follow;
import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.entity.embed.FollowId;
import com.delivalue.tidings.domain.data.repository.FollowRepository;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final TokenProvider tokenProvider;

    @Value("${STELLAGRAM_OFFICIAL_INTERNAL_ID}")
    private String STELLAGRAM_OFFICIAL_ID;

    public boolean checkUserExist(String id) {
        Optional<Member> member = this.memberRepository.findById(id);

        return member.isPresent();
    }

    public PublicIdValidateResponse checkPublicIdUsable(String publicId) {
        PublicIdValidateResponse response = new PublicIdValidateResponse(publicId);

        if(ForbiddenWordFilter.containsForbiddenWord(publicId)) {
            response.setResult(false);
            response.setStatusMessage("disableId");
        }

        if(response.isResult()) {
            Member member = this.memberRepository.findByPublicId(publicId);
            if(member != null) {
                response.setResult(false);
                response.setStatusMessage("alreadyTaken");
            }
        }

        return response;
    }

    @Transactional
    public LoginResponse registerMember(RegisterRequest newMemberData) {

        try {
            Member memberEntity = newMemberData.toEntity();
            this.memberRepository.save(memberEntity);

            Follow followEntity = new Follow(
                    new FollowId(this.STELLAGRAM_OFFICIAL_ID, newMemberData.getInternalId()),
                    LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            this.followRepository.save(followEntity);
            this.memberRepository.increaseFollowerCount(this.STELLAGRAM_OFFICIAL_ID);

            return LoginResponse.builder()
                    .result("login")
                    .refreshToken(this.tokenProvider.generateJWT(newMemberData.getInternalId(), "REFRESH"))
                    .accessToken(this.tokenProvider.generateJWT(newMemberData.getInternalId(), "ACCESS")).build();
        } catch (Exception e) {
            System.out.printf("\nregister error catch:\n" + e.toString() + "\n\n");

            return LoginResponse.builder()
                    .result("failed")
                    .refreshToken(null)
                    .accessToken(null).build();
        }
    }
}
