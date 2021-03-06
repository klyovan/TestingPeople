package com.netcracker.testerritto.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcracker.testerritto.models.LoginViewModel;
import com.netcracker.testerritto.properties.JwtProperties;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private AuthenticationManager authenticationManager;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
    LoginViewModel credentials = null;
    try {
      credentials = new ObjectMapper().readValue(request.getInputStream(), LoginViewModel.class);
    } catch (IOException e) {
      throw new AuthenticationCredentialsNotFoundException("Retrieving of credentials from inputStream was failed.", e);
    }
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
      credentials.getUserEmail(),
      credentials.getPassword(),
      new ArrayList<>());
    Authentication auth = authenticationManager.authenticate(authenticationToken);

    return auth;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
    UserPrincipal principal = (UserPrincipal) authResult.getPrincipal();
    String token = JWT.create()
      .withClaim("userId", principal.getUserId().toString())
      .withSubject(principal.getUsername())
      .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
      .sign(HMAC512(JwtProperties.SECRET.getBytes()));
    response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + token);
    response.addHeader(JwtProperties.HEADER_CUSTOM_HEADER, "*");
  }
}
