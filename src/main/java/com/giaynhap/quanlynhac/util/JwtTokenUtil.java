package com.giaynhap.quanlynhac.util;

import com.giaynhap.quanlynhac.model.Admin;
import com.giaynhap.quanlynhac.model.User;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.giaynhap.quanlynhac.config.AppConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
@Component
public class JwtTokenUtil implements Serializable {

    @Value("${jwt.secret}")
    private String secret;
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    public User getUser(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        String userUUID =  claims.get("uuid",String.class);
        String userName = claims.get("username",String.class);
        User user = new User();
        user.setAccount(userName);
        user.setUUID(userUUID);
        return user;
    }
    public boolean isAdminToken(String token){
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get("admin", boolean.class);
    }
    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    //check if the token has expired
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    //generate token for user
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username",user.getAccount());
        claims.put("uuid",user.getUUID());
        claims.put("admin",false);
        return doGenerateToken(claims, user.getAccount());
    }

    public String generateToken(Admin user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username",user.getUsername());
        claims.put("uuid",user.getUUID());
        claims.put("admin",true);
        return doGenerateToken(claims, user.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + AppConstant.JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public String refreshToken(String prevToken){
        final String username = getUsernameFromToken(prevToken);
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, username);
    }
    public String generateRefreshToken(String accessToken){
        Claims claims =  Jwts.parser().setSigningKey(secret).parseClaimsJws(accessToken).getBody();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String ptext = claims.getExpiration().toString() + claims.getSubject() + claims.getIssuedAt().toString();
            md.update(ptext.getBytes());
            StringBuffer sb = new StringBuffer();
            byte byteData[] = md.digest();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return  !isTokenExpired(token);
    }
}