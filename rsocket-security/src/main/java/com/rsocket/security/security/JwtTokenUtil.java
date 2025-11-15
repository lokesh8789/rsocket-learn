package com.rsocket.security.security;


import com.rsocket.security.util.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenUtil {

    public String getUsernameFromToken(String token) {
        log.info("Fetching username from token");
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        log.info("Fetching expiration from token");
        return getClaimFromToken(token,Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        log.info("Fetching claim from token");
        final Claims claims=getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        log.info("Fetching all claims from token");

        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Constants.SIGNING_KEY.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        log.info("is token expired");
        final Date expiration=getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(String userName) {
        log.info("Fetching generate token");
        return doGenerateToken(userName);
    }

    private String doGenerateToken(String subject) {
        log.info("do generate token");
        Claims claims = Jwts.claims()
                .subject(subject)
                .build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Constants.ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
                .signWith(Keys.hmacShaKeyFor(Constants.SIGNING_KEY.getBytes()), Jwts.SIG.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        log.info("validate token");
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
