package com.restapi.emp.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtService {
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    public static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;
    public static final int ACCESS_EXPIRE = 3600;

    //Access Token 파싱
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser() //JwtParserBuilder
                .verifyWith(KEY) //secret key 검증하기
                .build() //JwtParser
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Token에 포함된 email 주소와 DB에 저장된 email 주소가 같은지를 체크하고, token의 만료 여부 체크
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException(
                    "JWT was exprired or incorrect",
                    ex.fillInStackTrace());
        }
    }

    //Access Token 발행
    public String generateToken(String userName){
        // 토큰 만료시간 ACCESS_EXPIRE 3600초 => 60분
        Date exprireDate = Date.from(Instant.now()
                .plusSeconds(30));
                //.plusSeconds(ACCESS_EXPIRE));

        return Jwts.builder() //JwtBuilder
                .signWith(KEY, ALGORITHM) // Header에 Secret 값과 알고리즘 정보를 저장
                .subject(userName)  //Payload 의 subject에 email 주소 저장
                .issuedAt(new Date())  //토큰 발행 시간
                .expiration(exprireDate) //토큰 만료 시간 ( 현재 이후 시간 + 60분 = 3600초)
                .compact();
    }
}