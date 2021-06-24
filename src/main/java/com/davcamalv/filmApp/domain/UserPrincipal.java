package com.davcamalv.filmApp.domain;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails{
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String username;
	private String password;
	private String email;
	private Collection<? extends GrantedAuthority> authorities;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
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
	
	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public UserPrincipal(String name, String username, String password, String email,
			Collection<? extends GrantedAuthority> authorities) {
		super();
		this.name = name;
		this.username = username;
		this.password = password;
		this.email = email;
		this.authorities = authorities;
	}
	
	public static UserPrincipal build(User user) {
		List<GrantedAuthority> authorities = user.getRoles().stream().map(rol -> 
		new SimpleGrantedAuthority(rol.getRoleName().name())).collect(Collectors.toList());
		return new UserPrincipal(user.getName(), user.getUsername(), user.getPassword(), user.getEmail(), authorities);
	}
}
