package com.example.blog_project.jwt.filter;

import com.example.blog_project.jwt.exception.JwtExceptionCode;
import com.example.blog_project.jwt.token.JwtAuthenticationToken;
import com.example.blog_project.jwt.util.JwtTokenizer;
import com.example.blog_project.security.CustomUserDetails;
import com.example.blog_project.service.JwtBlacklistService;
import com.example.blog_project.service.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;

    // 인증 없이 접근할 수 있는 경로 목록
    private static final List<String> PERMIT_ALL_PATHS = List.of(
            "/signUp", "/signIn", "/", "/css/.*", "/js/.*", "/images/.*", "/api/login", "/api/.*"
            // 추가적으로 permitAll 경로들을 여기에 추가
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();

        // 인증이 필요 없는 경로인지 확인
        if (isPermitAllPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키에서 Access Token을 얻어냄
        String token = getToken(request);

        if (!StringUtils.hasText(token)) {
            // Access Token이 없는 경우 처리
            handleMissingToken(request, response);
        } else {
            // Access Token이 있는 경우 처리
            try {
                // 토큰이 블랙리스트에 있는지 확인
                if (jwtBlacklistService.isTokenBlacklisted(token)) {
                    // 토큰이 블랙리스트에 있으면 인증 실패 처리
                    handleException(request, response, JwtExceptionCode.BLACKLISTED_TOKEN, "Token is blacklisted: " + token);
                } else {
                    // 블랙리스트에 없으면 인증 정보 설정 시도
                    getAuthentication(token);
                    // 인증된 사용자의 정보를 헤더에 추가
                    addUserInfoToHeader(response);
                }
            } catch (ExpiredJwtException e) {
                // Access Token이 만료된 경우 Refresh Token 확인
                handleExpiredAccessToken(request, response, token, e);
            } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
                // 지원하지 않는 형식의 토큰 또는 잘못된 토큰 인 경우 처리
                handleException(request, response, JwtExceptionCode.INVALID_TOKEN, "Invalid token: " + token, e);
            } catch (Exception e) {
                // 그 외 다른 예외 발생 시
                handleException(request, response, JwtExceptionCode.UNKNOWN_ERROR, "JWT filter internal error: " + token, e);
            }
        }

        filterChain.doFilter(request, response);
    }

    // 요청 경로가 인증 없이 접근 가능한지 확인하는 메서드
    private boolean isPermitAllPath(String requestPath) {
        return PERMIT_ALL_PATHS.stream().anyMatch(requestPath::matches);
    }

    // Access Token이 없는 경우 처리하는 메서드
    private void handleMissingToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 쿠키에서 Refresh Token을 얻어냄
        String refreshToken = getRefreshToken(request);

        if (StringUtils.hasText(refreshToken) && !jwtTokenizer.isRefreshTokenExpired(refreshToken)) {
            // Refresh Token이 유효한 경우 새로운 Access Token 발급
            String newAccessToken = jwtTokenizer.refreshAccessToken(refreshToken);
            // 새로운 Access Token을 쿠키에 설정
            setAccessTokenCookie(response, newAccessToken);
            // 새로운 Access Token으로 인증 정보 설정
            getAuthentication(newAccessToken);
            // 새로 발급된 토큰 정보를 헤더에 추가
            addUserInfoToHeader(response);
        } else {
            // Refresh Token이 없거나 만료된 경우 처리할 로직 추가
            // 예를 들어, 로그인 페이지로 리다이렉트 또는 에러 처리 등
            // 여기에서는 예외를 던지지 않고 그냥 넘어가도록 설정함
        }
    }

    // Access Token이 만료된 경우 처리하는 메서드
    private void handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response, String token, ExpiredJwtException e) throws IOException {
        log.warn("Access token expired: {}", token);
        String refreshToken = getRefreshToken(request);
        if (StringUtils.hasText(refreshToken) && !jwtTokenizer.isRefreshTokenExpired(refreshToken)) {
            // Refresh Token이 유효한 경우 새로운 Access Token 발급
            String newAccessToken = jwtTokenizer.refreshAccessToken(refreshToken);
            // 새로운 Access Token을 쿠키에 설정
            setAccessTokenCookie(response, newAccessToken);
            // 새로운 Access Token으로 인증 정보 설정
            getAuthentication(newAccessToken);
            // 새로 발급된 토큰 정보를 헤더에 추가
            addUserInfoToHeader(response);
        } else {
            // Refresh Token도 만료된 경우
            handleException(request, response, JwtExceptionCode.EXPIRED_TOKEN, "Expired Token : " + token, e);
        }
    }

    // 쿠키에서 Access Token을 추출하는 메서드
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

    // 쿠키에서 Refresh Token을 추출하는 메서드
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

    // 새로운 Access Token을 쿠키에 설정하는 메서드
    private void setAccessTokenCookie(HttpServletResponse response, String newAccessToken) {
        Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
        accessTokenCookie.setHttpOnly(true); // XSS 보호를 위해 HttpOnly 설정
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));
        response.addCookie(accessTokenCookie);
    }

    // Access Token을 사용하여 인증 정보 설정하는 메서드
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

    // JWT Claims에서 권한 정보를 추출하는 메서드
    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        List<String> roles = (List<String>) claims.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(() -> role);
        }
        return authorities;
    }

    // 예외 처리 메서드
    private void handleException(HttpServletRequest request, HttpServletResponse response, JwtExceptionCode exceptionCode, String logMessage) throws IOException {
        handleException(request, response, exceptionCode, logMessage, null);
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, JwtExceptionCode exceptionCode, String logMessage, Exception e) throws IOException {
        request.setAttribute("exception", exceptionCode.getCode());
        log.error(logMessage, e);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized 설정
        response.getWriter().write("Authentication failed: " + exceptionCode.getMessage()); // 에러 메시지 반환
    }

    // 인증된 사용자의 정보를 헤더에 추가하는 메서드
    private void addUserInfoToHeader(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            response.setHeader("X-Username", userDetails.getUsername());
            response.setHeader("X-Roles", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(",")));
        }
    }
}