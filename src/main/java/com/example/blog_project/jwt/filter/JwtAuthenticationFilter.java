package com.example.blog_project.jwt.filter;

import com.example.blog_project.jwt.exception.JwtExceptionCode;
import com.example.blog_project.service.JwtBlacklistService;
import com.example.blog_project.jwt.token.JwtAuthenticationToken;
import com.example.blog_project.jwt.util.JwtTokenizer;
import com.example.blog_project.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenizer jwtTokenizer;
    private final JwtBlacklistService jwtBlacklistService;

    // permitAll 경로 목록 추가
    private static final List<String> PERMIT_ALL_PATHS = List.of(
            "/signUp", "/signIn", "/", "/css/.*", "/js/.*", "/images/.*", "/api/login", "/api/logout"
            // 추가적으로 permitAll 경로들을 여기에 추가
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();

        // permitAll 경로인지 확인
        if (isPermitAllPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Access token 얻어냄
        String token = getToken(request);

        if (!StringUtils.hasText(token)) {
            // 쿠키에서 토큰이 없는 경우 처리
            String refreshToken = getRefreshToken(request);
            if (StringUtils.hasText(refreshToken)) {
                try {
                    // Refresh Token이 유효한지 확인
                    if (!jwtTokenizer.isRefreshTokenExpired(refreshToken)) {
                        // Refresh Token이 유효한 경우 새로운 Access Token 발급
                        String newAccessToken = jwtTokenizer.refreshAccessToken(refreshToken);
                        // 새로운 Access Token을 쿠키에 설정
                        Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
                        accessTokenCookie.setHttpOnly(true); // XSS 보호를 위해 HttpOnly 설정
                        accessTokenCookie.setPath("/");
                        accessTokenCookie.setMaxAge(Math.toIntExact( JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));
                        response.addCookie(accessTokenCookie);
                        getAuthentication(newAccessToken);
                    } else {
                        // Refresh Token도 만료된 경우
                        request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
                        log.error("Refresh token expired or not found");
                        throw new BadCredentialsException("Expired refresh token");
                    }
                } catch (ExpiredJwtException e) {
                    // Refresh Token이 만료된 경우
                    request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
                    log.error("Expired refresh token", e);
                    throw new BadCredentialsException("Expired refresh token", e);
                }
            } else {
                // Refresh Token도 없는 경우
                request.setAttribute("exception", JwtExceptionCode.NOT_FOUND_TOKEN.getCode());
                log.error("Token not found in request");
                throw new BadCredentialsException("Token not found in request");
            }
        } else {
            try {
                // AccessToken 검증
                if (jwtBlacklistService.isTokenBlacklisted(token)) {
                    // 토큰이 블랙리스트에 있으면 인증 실패 처리
                    request.setAttribute("exception", JwtExceptionCode.BLACKLISTED_TOKEN.getCode());
                    log.error("Token is blacklisted: {}", token);
                    throw new BadCredentialsException("Token is blacklisted");
                }

                // 인증 설정 시도
                getAuthentication(token);
            } catch (ExpiredJwtException e) {
                // Access Token이 만료된 경우 Refresh Token 확인
                System.out.println("accessToken 만료");
                String refreshToken = getRefreshToken(request);
                if (StringUtils.hasText(refreshToken) && !jwtTokenizer.isRefreshTokenExpired(refreshToken)) {
                    System.out.println("accessToken 재발급 받으러 들어옴");

                    // Refresh Token이 유효한 경우 새로운 Access Token 발급
                    String newAccessToken = jwtTokenizer.refreshAccessToken(refreshToken);
                    // 새로운 Access Token을 쿠키에 설정
                    Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
                    accessTokenCookie.setHttpOnly(true); // XSS 보호를 위해 HttpOnly 설정
                    accessTokenCookie.setPath("/");
                    response.addCookie(accessTokenCookie);
                    getAuthentication(newAccessToken);
                } else {
                    System.out.println("accessToken 재발급 실패");

                    // Refresh Token도 만료된 경우
                    request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
                    log.error("Expired Token : {}", token, e);
                    throw new BadCredentialsException("Expired token exception", e);
                }
            } catch (UnsupportedJwtException e) {
                request.setAttribute("exception", JwtExceptionCode.UNSUPPORTED_TOKEN.getCode());
                log.error("Unsupported Token: {}", token, e);
                throw new BadCredentialsException("Unsupported token exception", e);
            } catch (MalformedJwtException e) {
                request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN.getCode());
                log.error("Invalid Token: {}", token, e);
                throw new BadCredentialsException("Invalid token exception", e);
            } catch (IllegalArgumentException e) {
                request.setAttribute("exception", JwtExceptionCode.NOT_FOUND_TOKEN.getCode());
                log.error("Token not found: {}", token, e);
                throw new BadCredentialsException("Token not found exception", e);
            } catch (Exception e) {
                log.error("JWT Filter - Internal Error: {}", token, e);
                throw new BadCredentialsException("JWT filter internal exception", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPermitAllPath(String requestPath) {
        for (String permitPath : PERMIT_ALL_PATHS) {
            if (requestPath.matches(permitPath)) {
                return true;
            }
        }
        return false;
    }

    private void getAuthentication(String token) {
        Claims claims = jwtTokenizer.parseAccessToken(token);
        String email = claims.getSubject();
        Long userId = claims.get("userId", Long.class);
        String name = claims.get("name", String.class);
        String username = claims.get("username", String.class);
        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

        CustomUserDetails userDetails = new CustomUserDetails(username, "", name, authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        Authentication authentication = new JwtAuthenticationToken(authorities, userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        List<String> roles = (List<String>) claims.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(() -> role);
        }
        return authorities;
    }

    private String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // Refresh Token을 쿠키에서 추출하는 메서드
    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
