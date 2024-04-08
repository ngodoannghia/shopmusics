package com.giaynhap.quanlynhac;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.giaynhap.quanlynhac.manager.MusicManager;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


import java.util.Properties;

@SpringBootApplication
public class QuanlydataApplication  {

	@Value("${spring.mail.host}")
	String host;
	@Value("${spring.mail.port}")
	String port;
	@Value("${spring.mail.username}")
	String username;
	@Value("${spring.mail.password}")
	String password;
	@Value("${spring.mail.app2.password}")
	String passwordApp2;
	@Value("${spring.mail.transport.protocol}")
	String protocol;
	@Value("${spring.mail.smtp.starttls.enable}")
	String starttls;
	@Value("${spring.mail.smtp.auth}")
	String auth;

	@Bean
	public BCrypt.Hasher bCryptPasswordEncoder() {

		return BCrypt.withDefaults();
	}
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return mapper;
	}
	@Bean
	public MusicManager musicManager(){
		MusicManager musicManager = new MusicManager();
		return musicManager;
	}

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(10);
		taskScheduler.initialize();
		return taskScheduler;
	}
	@Bean
	public JavaMailSender javaMailService1() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(host);
		javaMailSender.setPassword(password);
		javaMailSender.setUsername(username);
		try {
			javaMailSender.setPort(Integer.parseInt(port));
		}catch (Exception e){
			System.out.println("port "+port);
			javaMailSender.setPort(587);
		}

		Properties mailProps = new Properties();
		mailProps.put("mail.smtp.auth", auth);
		mailProps.put("mail.transport.protocol", protocol);
		mailProps.put("mail.smtp.starttls.enable", starttls);
		mailProps.put("mail.smtp.connectiontimeout", 5000);
		mailProps.put("mail.smtp.timeout", 5000);
		mailProps.put("mail.smtp.writetimeout",5000);
		javaMailSender.setJavaMailProperties(mailProps);

		return javaMailSender;
	}

	@Bean
	public JavaMailSender javaMailService2() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(host);
		javaMailSender.setPassword(passwordApp2);
		javaMailSender.setUsername(username);
		try {
			javaMailSender.setPort(Integer.parseInt(port));
		}catch (Exception e){
			System.out.println("port "+port);
			javaMailSender.setPort(587);
		}

		Properties mailProps = new Properties();
		mailProps.put("mail.smtp.auth", auth);
		mailProps.put("mail.transport.protocol", protocol);
		mailProps.put("mail.smtp.starttls.enable", starttls);
		mailProps.put("mail.smtp.connectiontimeout", 5000);
		mailProps.put("mail.smtp.timeout", 5000);
		mailProps.put("mail.smtp.writetimeout",5000);
		javaMailSender.setJavaMailProperties(mailProps);

		return javaMailSender;
	}

	public static void main(String[] args) {

		SpringApplication.run(QuanlydataApplication.class, args);
	}

}
