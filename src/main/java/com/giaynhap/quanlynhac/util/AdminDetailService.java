package com.giaynhap.quanlynhac.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.giaynhap.quanlynhac.model.Admin;
import com.giaynhap.quanlynhac.repository.AdminRepository;

public class AdminDetailService implements UserDetailsService {
	@Autowired
	private AdminRepository adminRepository;
	@Override
	public AdminUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Admin user = adminRepository.findByUserName(username);
		if (user == null) {
	    	throw new UsernameNotFoundException("User not exists by Username" + username);
	    }
		return AdminUserDetail.build(user);
	}

}
