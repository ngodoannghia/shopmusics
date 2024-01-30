package com.giaynhap.quanlynhac.dto;

import com.giaynhap.quanlynhac.config.AppConstant;

public class AuthenResponse<T> {
    private  String jwttoken;
    private  String refreshToken;
    private T user;


    public AuthenResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }
    public AuthenResponse(String jwttoken,String refreshToken) {
        this.jwttoken = jwttoken;
        this.refreshToken = refreshToken;
    }


    public String getRefreshToken() {
        return this.refreshToken;
    }
    public long getDuringTime(){
        return AppConstant.JWT_TOKEN_VALIDITY;
    }

    public String getJwttoken() {
        return jwttoken;
    }

    public void setJwttoken(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public T getUser() {
        return user;
    }

    public void setUser(T user) {
        this.user = user;
    }
}
