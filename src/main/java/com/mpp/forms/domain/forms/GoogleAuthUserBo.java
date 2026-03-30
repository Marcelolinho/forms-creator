package com.mpp.forms.domain.forms;

import lombok.Data;

@Data
public class GoogleAuthUserBo {

    private String authenticationUrl;
    private Boolean authenticated;

    public String getAuthenticationUrl() {
        if (!authenticated) {
            return authenticationUrl;
        }
        return null;
    }
}
