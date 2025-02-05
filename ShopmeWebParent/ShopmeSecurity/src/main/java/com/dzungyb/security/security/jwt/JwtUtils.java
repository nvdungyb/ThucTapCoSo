package com.security.security.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
@Getter
public class JwtUtils {
    @Value("${accessTokenSecret}")
    private String accessTokenSecret;

    @Value("${refreshTokenSecret}")
    private String refreshTokenSecret;

    /*
        Jwt: Header (type of Token - JWT && signing algorithm - SHA256)
             PayLoad (contains claims - iss, exp, sub...)
             Signature (have to encoded header and payload. ex: signature = SHA256(base64.encode(header) + "." + base64.encode(payload), secret)

        User agent should send the JWT in the Authorization header using Bearer schema.
            ex - Authorization: Bearer <token>

     */

    public boolean validateJwtToken(String jwtToken, String tokenSecret) {
        Date timeJwtTokenExpired = extractClaim(jwtToken, tokenSecret, Claims::getExpiration);
        return timeJwtTokenExpired.after(new Date());
    }

    public String getEmailFromAccessToken(String jwtToken) {
        return extractClaim(jwtToken, accessTokenSecret, Claims::getSubject);
    }

    private <T> T extractClaim(String jwtToken, String tokenSecret, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken, tokenSecret);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken, String tokenSecret) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey(tokenSecret))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    public String generateAccessToken(String email, long jwtExpirationMs) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, getSignKey(accessTokenSecret))
                .compact();
    }

    public String generateRefreshToken(String userId, long refreshTokenValidity) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(SignatureAlgorithm.HS256, getSignKey(refreshTokenSecret))
                .compact();
    }

    private Key getSignKey(String tokenSecret) {
        byte[] secretBytes = Decoders.BASE64.decode(tokenSecret);
        return Keys.hmacShaKeyFor(secretBytes);
    }

    public boolean isValidRefreshToken(String refreshToken) {
        return validateJwtToken(refreshToken, refreshTokenSecret);
    }

    public Long getUserIdFromRefreshToken(String refreshToken) {
        return Long.parseLong(extractClaim(refreshToken, refreshTokenSecret, Claims::getSubject));
    }
}
