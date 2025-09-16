package com.film.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.method.P;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    final String path = request.getServletPath();
    final String method = request.getMethod();

    List<EndpointRule> rules = List.of(
        new EndpointRule("/api/auth", null),
        new EndpointRule("/api/films", "GET"));

    return rules.stream().anyMatch(rule -> rule.matches(path, method));
  }

  private static class EndpointRule {
    private final String prefix;
    private final String method; // null == all method

    public EndpointRule(String prefix, String method) {
      this.prefix = prefix;
      this.method = method;
    }

    public boolean matches(String path, String requestMethod) {
      boolean pathMatches = path.startsWith(prefix);
      boolean methodMatches = (method == null) || method.equalsIgnoreCase(requestMethod);
      return pathMatches && methodMatches;
    }
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // String authHeader = request.getHeader("Authorization");
    String token = null;
    String username = null;

    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("jwt".equals(cookie.getName())) {
          token = cookie.getValue();
          break;
        }
      }
    }

    if (token != null) {
      username = jwtUtil.extractUsername(token);
    }

    // if (authHeader != null && authHeader.startsWith("Bearer ")) {
    // token = authHeader.substring(7);
    // username = jwtUtil.extractUsername(token);
    // }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      if (jwtUtil.validateToken(token)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}
