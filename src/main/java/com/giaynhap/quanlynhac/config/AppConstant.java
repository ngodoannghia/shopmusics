package com.giaynhap.quanlynhac.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConstant {
    public static final String SUCCESS_MESSAGE = "success";
    public static final String  ERROR_MESSAGE = "failed";
    public static final String BAD_REQUEST_MESSAGE = "bad reqeuest";
    public static final long JWT_TOKEN_VALIDITY = 60*60*24;
    public static final String SC_UNAUTHORIZED = "UNAUTHORIZED";
    @Value("${path.avatar}")
    public String avatarPath;

    @Value("${path.music}")
    public String musicPath;

    @Value("${host.image}")
    public String hostImage;

    @Value("${host.audio}")
    public String hostAudio;

    @Value("${host.host}")
    public String hostServer;

    @Value("${amazon.music}")
    public String music;
    @Value("${amazon.photo}")
    public String photo;
    @Value("${amazon.musicDemo}")
    public String musicDemo;
    @Value("${amazon.musicStream}")
    public String musicStream;
    @Value("${giaynhap.stream.disable}")
    public String disableStream;

    public enum MusicType {
        ORIGIN(1), DEMO(2);
        private final int value;
        private MusicType(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }
}
