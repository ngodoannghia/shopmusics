package com.giaynhap.quanlynhac.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.giaynhap.quanlynhac.util.LocalDateTimeDeserializer;
import com.giaynhap.quanlynhac.util.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "user")
@Table(name = "user")
public class User {
    @Id
    @Column(name = "uuid", length=100)
    private String UUID;
    @Column(name="username", length=255)
    private String username;
    @Column(name = "password", length=255)
    private  String password;
    @Column(name = "email", length=255)
    private String email;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "create_at")
    private LocalDateTime create_at;
    @Column(name = "enable")
    private  boolean enable;
    @Column(name = "ip_address", length=255)
    private  String ipAddress;
    @Column(name = "current_token", length=255)
    private String currentToken;
    @Column(name = "expire_token")
    private Long expireToken;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "uuid", nullable=true)
    private UserInfo info;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    } 
    public LocalDateTime getCreate_at() {
        return create_at;
    }

    public void setCreate_at(LocalDateTime create_at) {
        this.create_at = create_at;
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public void setCurrentToken(String currentToken) {
        this.currentToken = currentToken;
    }

    public Long getExpireToken() {
        return expireToken;
    }

    public void setExpireToken(Long expireToken) {
        this.expireToken = expireToken;
    }
    
}
