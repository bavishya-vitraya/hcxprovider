package com.hcx.hcxprovider.util;

import com.nimbusds.jwt.JWTClaimsSet;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil implements Serializable {

    @Value("${app.secret}")
    private String secret;

    public boolean validateToken(String token, UserDetails userDetails){
        String tokenUsername= getUserName(token);
        return (tokenUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    public boolean isTokenExpired(String token){
       Date expDate= getExpirationDate(token);
       return  expDate.before(new Date(System.currentTimeMillis()));
    }
    public String getUserName(String token){
        return getClaims(token).getSubject();
    }

    public Date getExpirationDate(String token){
        return getClaims(token).getExpiration();
    }
    public Claims getClaims(String token){
        return  Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
    }

    public String generateToken(UserDetails userDetails){
         return Jwts.builder().setSubject(userDetails.getUsername()).setIssuer("Joshima")
                .setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)))
                .signWith(SignatureAlgorithm.HS512,secret.getBytes(StandardCharsets.UTF_8)).compact();
    }
}
