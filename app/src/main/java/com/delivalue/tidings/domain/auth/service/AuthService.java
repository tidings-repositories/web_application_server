package com.delivalue.tidings.domain.auth.service;

import com.delivalue.tidings.common.ForbiddenWordFilter;
import com.delivalue.tidings.common.TokenProvider;
import com.delivalue.tidings.domain.auth.dto.LoginResponse;
import com.delivalue.tidings.domain.auth.dto.PublicIdValidateResponse;
import com.delivalue.tidings.domain.auth.dto.RegisterRequest;
import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public AuthService(MemberRepository memberRepository, TokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
    }

    public boolean checkUserExist(String id) {
        Optional<Member> member = memberRepository.findById(id);

        return member.isPresent();
    }

    public PublicIdValidateResponse checkPublicIdUsable(String publicId) {
        PublicIdValidateResponse response = new PublicIdValidateResponse(publicId);

        if(ForbiddenWordFilter.containsForbiddenWord(publicId)) {
            response.setResult(false);
            response.setStatusMessage("disableId");
        }

        if(response.isResult()) {
            Member member = memberRepository.findByPublicId(publicId);
            if(member != null) {
                response.setResult(false);
                response.setStatusMessage("alreadyTaken");
            }
        }

        return response;
    }

    public LoginResponse registerMember(RegisterRequest newMemberData) {
        try {
            Member memberEntity = newMemberData.toEntity();
            memberRepository.save(memberEntity);

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
