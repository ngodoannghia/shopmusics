package com.giaynhap.quanlynhac.service;

import com.giaynhap.quanlynhac.config.AppConstant;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class UtilServiceIml  implements UtilService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private AppConstant appConstant;
    @Value("${spring.mail.sender}")
    public String sender;
    @Override
    public void sendVeryEmail(String token,String toEmail) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper  = new MimeMessageHelper(mimeMessage, false, "utf-8");

            String query = URLEncoder.encode(token, StandardCharsets.UTF_8.toString());
            helper.setSubject("Xác thực tài khoản Shop music");
            helper.setFrom(sender);
            helper.setTo(toEmail);
            helper.setText("<b>Email tự động ứng dụng shop music</b>. <br> Để xác thực bạn cần thực hiện: <br>Truy cập : <a href=" + appConstant.hostImage + "/very/email?token="+query+"> Xác thực </a>",true);

            javaMailSender.send(mimeMessage);
        }catch (Exception e){
                e.printStackTrace();
        }
    }
    @Override
    public void sendVeryLostPassword(String otp,String toEmail) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper  = new MimeMessageHelper(mimeMessage, false, "utf-8");

            helper.setSubject("Xác thực tài khoản Shop music");
            helper.setFrom(sender);
            helper.setTo(toEmail);
            helper.setText("Bạn đã yêu cầu lấy lại mật khẩu, mật khẩu mới của bạn là <b>"+otp+"</b>",true);

            javaMailSender.send(mimeMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendNotify(String body,String title,String topic){
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody( body)
                        .build()) .setTopic(topic)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {

        }
    }
    public void sendNotiPublish(String name){
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("Dành cho bạn")
                        .setBody("Vừa có bài hát mới được đăng tải: "+name)
                        .build()) .setTopic("NewDemo")
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {

        }
    }
}
