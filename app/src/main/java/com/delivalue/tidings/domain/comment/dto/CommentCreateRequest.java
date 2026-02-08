package com.delivalue.tidings.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentCreateRequest {
    @NotBlank
    private String text;
}
