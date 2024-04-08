package com.giaynhap.quanlynhac.service;

import com.giaynhap.quanlynhac.config.AppConstant;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


@Component
public class UtilServiceIml  implements UtilService {
    // @Autowired
    // private JavaMailSender javaMailSender;
    @Autowired
    @Qualifier("javaMailService1")
    public JavaMailSender mailSenderForApp1;

    @Autowired
    @Qualifier("javaMailService2")
    public JavaMailSender mailSenderForApp2;

    @Autowired
    private AppConstant appConstant;
    @Value("${spring.mail.sender}")
    public String sender;

    @Override
    public void sendVeryEmail(String token,String toEmail, String username) {
        
        try {
            MimeMessage mimeMessage = mailSenderForApp1.createMimeMessage();
            MimeMessageHelper helper  = new MimeMessageHelper(mimeMessage, false, "utf-8");

            String query = URLEncoder.encode(token, StandardCharsets.UTF_8.toString());
            String link = appConstant.hostServer + "very/email?token=" + query;
            System.out.println("Link: " + link);
            helper.setSubject("Email xác thực thông tin khách hàng <strong>" + username + "<strong>");
            helper.setFrom(sender);
            helper.setTo(toEmail);
            // helper.setText("<b>Email tự động ứng dụng shop music</b>. <br> Để xác thực bạn cần thực hiện: <br>Truy cập : <a href=" + link + "Xác thực </a>",true);
            String text = "<html>" + 
                        "<table cellpadding=\"0\" cellspacing=\"0\", width=\"100%\" style=\"max-width:600px\">" + 
                        "<tbody>" +
                        "<tr>" + 
                        "<td style=\"padding:20px;border-bottom:2px solid #dddddd\" bgcolor=\"#ffffff\">" + 
                        "<p>Mã email: <strong>" + UUID.randomUUID().toString() + "</strong></p>" +
                        "<p>Xin chào <strong>" + username + "</strong></p>" + 
                        "<p> Cảm ơn khách hàng đã đăng ký tài khoản trên Shop Music. Để có được trải nghiệm dịch vụ và được hỗ trợ tốt nhất, bạn cần hoàn thiện xác thực tài khoản.</p>" +
                        "<p>Vui lòng bấm nút Xác thực để hoàn tất quá trình này</p>" +
                        "<p style=\"text-align: center\">" +
                        "<a href=\"" + link + "\" target=\"_blank\" style=\"color:#fff;text-decoration:none;display:inline-block;background-color:#00b14f;padding:12px 20px;font-weight:bold;border-radius:4px\">" +
                        "<strong>Xác thực</strong>" +
                        "</a></p>" +
                        "<p>Liên hệ với chúng tôi để được hỗ trợ nhiều hơn:<br>Hotline: <strong>00390490903</strong><br>Email:<strong><a href=\"mailto:nghiango@gmail.com\" target=\"_blank\">nghiango@gmail.com</a></strong></p>" + 
                        "<p><strong>Trân trọng<br>Shop Music</strong></p>" + 
                        "</td>" +
                        "</tr>" + 
                        "</tbody>" + 
                        "</table>" + 
                        "</html>";
            helper.setText(text, true);

            mailSenderForApp1.send(mimeMessage);
        }catch (Exception e){
                e.printStackTrace();
        }
    }
    @Override
    public void sendVeryLostPassword(String otp,String toEmail) {
        try {
            // JavaMailSender javaMailSender2 = mailService.javaMailSenderApp2();
            MimeMessage mimeMessage = mailSenderForApp2.createMimeMessage();
            MimeMessageHelper helper  = new MimeMessageHelper(mimeMessage, false, "utf-8");

            helper.setSubject("Xác thực tài khoản Shop music");
            helper.setFrom(sender);
            helper.setTo(toEmail);
            helper.setText("Bạn đã yêu cầu lấy lại mật khẩu, mật khẩu mới của bạn là <b>"+otp+"</b>",true);

            mailSenderForApp2.send(mimeMessage);
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
            @SuppressWarnings("unused")
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
            @SuppressWarnings("unused")
			String response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {

        }
    }
}
