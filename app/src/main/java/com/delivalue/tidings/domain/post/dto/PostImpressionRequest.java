package com.delivalue.tidings.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostImpressionRequest {
    @Pattern(regexp = "HOME_FEED|OON_FEED|SEARCH|PROFILE|NOTIFICATION")
    @JsonProperty("display_location")
    private String displayLocation;

    @JsonProperty("is_linger")
    private Boolean isLinger = false;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    @JsonProperty("watch_completion_rate")
    private Double watchCompletionRate;

    @Min(0)
    @JsonProperty("dwell_time_ms")
    private Long dwellTimeMs;

    @JsonProperty("is_skipped")
    private Boolean isSkipped = false;

    @Pattern(regexp = "NONE|2S|5S|10S|30S")
    @JsonProperty("click_dwell_bucket")
    private String clickDwellBucket;

    public Boolean getIsLinger() { return isLinger != null ? isLinger : false; }
    public Boolean getIsSkipped() { return isSkipped != null ? isSkipped : false; }
}
