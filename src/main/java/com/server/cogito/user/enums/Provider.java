package com.server.cogito.user.enums;

import com.server.cogito.common.exception.infrastructure.UnsupportedOauthProviderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Provider {

    KAKAO("kakao"),
    GITHUB("github");

    private String name;
    private static final Map<String,String> TYPES = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(Provider::getName, Provider::name)));

    public static Provider toEnum(String provider){
        if(Arrays.stream(values()).noneMatch(p->p.name.equals(provider)))
            throw new UnsupportedOauthProviderException();
        return valueOf(TYPES.get(provider));
    }

    public boolean isSameAs(Provider provider) {
        return this.equals(provider);
    }
}
