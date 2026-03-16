package com.delivalue.tidings.domain.block.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlockRequest {
    @NotBlank
    private String publicId;
}
