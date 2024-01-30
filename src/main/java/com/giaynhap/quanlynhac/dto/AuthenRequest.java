package com.giaynhap.quanlynhac.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class AuthenRequest implements Serializable {
     @JsonProperty("username")
    private String username;
     @JsonProperty("password")
    private String password;
    public AuthenRequest(){

    }
    public AuthenRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
