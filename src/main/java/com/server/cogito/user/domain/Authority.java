package com.server.cogito.user.domain;

import lombok.ToString;

@ToString
public enum Authority {
    ROLE_USER,
    ROLE_MEMBERSHIP,
    ROLE_ADMIN
}
