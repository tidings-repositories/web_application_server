package com.delivalue.tidings.domain.auth.dto;

import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicIdValidateResponse {
    private boolean result;
    private String statusMessage;

    public PublicIdValidateResponse(String expectId) {
        this.validate(expectId);
    }

    private void validate(String expectId) {
        Pattern publicIdPattern = Pattern.compile("^[A-Za-z0-9_]+$");
        boolean isOverRangeString = expectId.length() < 4 || expectId.length() > 15;
        boolean isSupportCharacter = publicIdPattern.matcher(expectId).matches();

        if (isOverRangeString) {
            this.result = false;
            this.statusMessage = "overRangeString";
        }

        if (!isSupportCharacter) {
            this.result = false;
            this.statusMessage = "noSupportCharacter";
        }

        if(!isOverRangeString && isSupportCharacter) {
            this.result = true;
            this.statusMessage = "enableId";
        }
    }
}
