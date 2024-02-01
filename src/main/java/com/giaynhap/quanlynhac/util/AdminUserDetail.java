package com.giaynhap.quanlynhac.util;

import java.util.Collection;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.giaynhap.quanlynhac.model.Admin;

public class AdminUserDetail implements UserDetails {
  private static final long serialVersionUID = 1L;
	  
	  private String id;

	  private String username;

	  @JsonIgnore
	  private String password;

	  private Collection<? extends GrantedAuthority> authorities;

	  public AdminUserDetail(String id, String username, String password) {
	    this.id = id;
	    this.username = username;
	    this.password = password;
	  }

	  public static AdminUserDetail build(Admin user) {
	    return new AdminUserDetail(
	        user.getUUID(), 
	        user.getUsername(), 
	        user.getPassword());
	  }

	  @Override
	  public Collection<? extends GrantedAuthority> getAuthorities() {
	    return authorities;
	  }

	  public String getId() {
	    return id;
	  }


	  @Override
	  public String getPassword() {
	    return password;
	  }

	  @Override
	  public String getUsername() {
	    return username;
	  }

	  @Override
	  public boolean isAccountNonExpired() {
	    return true;
	  }

	  @Override
	  public boolean isAccountNonLocked() {
	    return true;
	  }

	  @Override
	  public boolean isCredentialsNonExpired() {
	    return true;
	  }

	  @Override
	  public boolean isEnabled() {
	    return true;
	  }

	  @Override
	  public boolean equals(Object o) {
	    if (this == o)
	      return true;
	    if (o == null || getClass() != o.getClass())
	      return false;
	    AdminUserDetail user = (AdminUserDetail) o;
	    return Objects.equals(id, user.id);
	  }
}
