package com.giaynhap.quanlynhac.dto;

import java.time.LocalDateTime;

public class AdminDTO {
	private String username;
	
	private  String password;
	
	private LocalDateTime create_at;

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

	public LocalDateTime getCreate_at() {
		return create_at;
	}

	public void setCreate_at(LocalDateTime create_at) {
		this.create_at = create_at;
	}
}
