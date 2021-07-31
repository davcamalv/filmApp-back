package com.davcamalv.filmApp.security;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.davcamalv.filmApp.domain.UserPrincipal;
import com.davcamalv.filmApp.dtos.JwtDTO;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtProvider {

	protected final Logger log = Logger.getLogger(JwtProvider.class);

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private int expiration;

	public String generateToken(Authentication authentication) {
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		List<String> roles = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());
		return Jwts.builder().setSubject(userPrincipal.getUsername())
				.claim("roles", roles)
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + expiration * 1000))
				.signWith(SignatureAlgorithm.HS512, secret.getBytes()).compact();
	}

	public String getUsername(String token) {
		return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
			return true;
		} catch (MalformedJwtException e) {
			log.error("Malformed token");
		} catch (UnsupportedJwtException e) {
			log.error("Token not supported");
		} catch (ExpiredJwtException e) {
			log.error("Token expired");
		} catch (IllegalArgumentException e) {
			log.error("Token empty");
		} catch (SignatureException e) {
			log.error("Signature exception");
		}
		return false;
	}
	
	public String refreshToken(JwtDTO jwtDTO) throws ParseException {
		JWT jwt = JWTParser.parse(jwtDTO.getToken());
		JWTClaimsSet claims = jwt.getJWTClaimsSet();
		String username = claims.getSubject();
		@SuppressWarnings("unchecked")
		List<String> roles = (List<String>) claims.getClaim("roles");
		return Jwts.builder().setSubject(username)
				.claim("roles", roles)
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + expiration * 1000))
				.signWith(SignatureAlgorithm.HS512, secret.getBytes()).compact();
	}
}
