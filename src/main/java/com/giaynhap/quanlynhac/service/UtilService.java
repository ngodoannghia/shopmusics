package com.giaynhap.quanlynhac.service;

import org.springframework.stereotype.Service;

@Service
public interface UtilService {
     void sendVeryEmail(String token,String toEmail, String username);
     void sendVeryLostPassword(String otp,String toEmail);
      void sendNotiPublish(String name);
      void sendNotify(String body,String title,String topic);
}
