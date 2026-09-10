package com.ewsaly.ewsaly.security.jwt;

import com.ewsaly.ewsaly.models.User;
import com.ewsaly.ewsaly.security.user.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import io.jsonwebtoken.Jwts;

@Slf4j
@Component
public class JwtUtils {

    @Value("${auth.token.jwt-secret-key}")
    private String jwtSecretKey;

    @Value("${auth.token.expiration-in-mils}")
    private int expirationTime;

    public String generateTokenForUser(User user){

        String role = user.getRole().toString();

        return Jwts.builder()
                .setSubject(user.getPhoneNumber())
                .claim("id",user.getId())
                .claim("role",role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expirationTime))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromToken(String token){
        return  Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);

            return true;}
        catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            throw new JwtException("expired");
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT: {}", e.getMessage());
            throw new JwtException("Invalid token");
        }
    }

    public Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }

}
