package com.delivalue.tidings.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String result;
    private String refreshToken;
    private String accessToken;
}
