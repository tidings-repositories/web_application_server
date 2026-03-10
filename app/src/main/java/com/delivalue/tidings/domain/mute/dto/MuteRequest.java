package com.delivalue.tidings.domain.mute.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MuteRequest {
    @NotBlank
    private String publicId;
}
