package com.server.cogito.global.common.security.jwt;

import com.server.cogito.domain.auth.dto.TokenResponse;
import com.server.cogito.global.common.exception.auth.AuthErrorCode;
import com.server.cogito.global.common.exception.auth.TokenException;
import com.server.cogito.global.common.security.AuthUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;


@Slf4j
@Component
public class JwtProvider implements InitializingBean {

    private final String secret;
    private final String AUTHORITIES_KEY = "auth";
    private final String USER_ID = "userId";
    private final String USER_PROVIDER = "provider";
    private final String USERNAME = "username";
    private final long ACCESS_TOKEN_EXPIRE_TIME;
    private final long REFRESH_TOKEN_EXPIRE_TIME;
    private Key key;



    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.access-expiration-time}") long accessTokenExpirationMilliseconds,
                       @Value("${jwt.refresh-expiration-time}") long refreshTokenExpirationMilliseconds){
        this.secret = secret;
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTokenExpirationMilliseconds;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTokenExpirationMilliseconds;
    }


    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenResponse createToken(AuthUser authUser) {

        Date now = new Date();

        String accessToken = Jwts.builder()
                .setSubject(authUser.getUsername())
                .setClaims(createClaimsByAuthUser(authUser))
                .setIssuedAt(now)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private Map<String, Object> createClaimsByAuthUser(AuthUser authUser) {
        Map<String, Object> map = new HashMap<>();
        map.put(USER_ID, authUser.getUserId());
        map.put(USER_PROVIDER, authUser.getProvider());
        map.put(USERNAME, authUser.getUsername());
        return map;
    }

    public String getUserEmail(String token) {
        return getClaims(token)
                .get(USERNAME, String.class);
    }

    private Claims getClaims(String token) {
        return parse(token)
                .getBody();
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(token);
    }

    public boolean validateToken(String token) {
        try {
            parse(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
            throw new TokenException(AuthErrorCode.INVALID_SIGNATURE);
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
            throw new TokenException(AuthErrorCode.INVALID_JWT);
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            throw new TokenException(AuthErrorCode.EXPIRED);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            throw new TokenException(AuthErrorCode.UNSUPPORTED);
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
            throw new TokenException(AuthErrorCode.EMPTY_CLAIM);
        }


    }

    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}