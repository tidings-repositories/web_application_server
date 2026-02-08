package com.delivalue.tidings.domain.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateBody {
	@JsonProperty("user_name")
	private String userName;
	private String bio;
	@JsonProperty("profile_image")
	private String profileImage;
	private Integer badge;
}
