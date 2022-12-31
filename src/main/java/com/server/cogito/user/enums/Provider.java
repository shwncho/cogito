package com.server.cogito.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Provider {

    KAKAO("KAKAO"),
    GITHUB("GITHUB");

    private final String provider;
}
