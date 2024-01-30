package com.giaynhap.quanlynhac.controller;

import com.giaynhap.quanlynhac.config.AppConstant;
import com.giaynhap.quanlynhac.manager.MusicManager;
import com.giaynhap.quanlynhac.model.User;
import com.giaynhap.quanlynhac.model.UserStore;
import com.giaynhap.quanlynhac.service.FileService;
import com.giaynhap.quanlynhac.service.UserService;
import com.giaynhap.quanlynhac.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Component
public class MusicSocketHandler implements WebSocketHandler {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    UserService userService;
    @Autowired
    FileService fileService;
    @Autowired
    MusicManager musicManager;
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
     String  token = webSocketSession.getAttributes().get("token").toString();
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
