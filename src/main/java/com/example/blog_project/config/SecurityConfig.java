package com.example.blog_project.config;

import com.example.blog_project.jwt.exception.CustomAuthenticationEntryPoint;
import com.example.blog_project.jwt.filter.JwtAuthenticationFilter;
import com.example.blog_project.jwt.util.JwtTokenizer;
import com.example.blog_project.security.CustomUserDetailsService;
import com.example.blog_project.service.JwtBlacklistService;
import com.example.blog_project.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtBlacklistService jwtBlacklistService;
    private final RefreshTokenService refreshTokenService;

    /**
     * SecurityFilterChain 빈을 정의합니다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 인가 규칙을 설정합니다.
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/signUp", "/signIn", "/", "/css/**", "/js/**", "/images/**", "/api/login", "/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                // UsernamePasswordAuthenticationFilter 앞에 JWT 인증 필터를 추가합니다.
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer, jwtBlacklistService, refreshTokenService), UsernamePasswordAuthenticationFilter.class)
                // 폼 로그인을 비활성화합니다. (JWT를 사용하여 인증을 수행합니다)
                .formLogin(form -> form.disable())
                // 세션 관리를 상태 없음으로 설정합니다. (세션을 사용하지 않음)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // CSRF 보호를 비활성화합니다.
                .csrf(csrf -> csrf.disable())
                // HTTP 기본 인증을 비활성화합니다.
                .httpBasic(httpBasic -> httpBasic.disable())
                // CORS (Cross-Origin Resource Sharing)를 구성합니다.
                .cors(cors -> cors.configurationSource(configurationSource()))
                // 사용자 정의 인증 진입 지점을 처리하기 위한 예외 처리를 구성합니다.
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint));

        return http.build();
    }

    /**
     * 모든 origin, header, method를 허용하는 CORS 구성 소스를 정의합니다.
     */
    public CorsConfigurationSource configurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowedMethods(List.of("GET", "POST", "DELETE"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * BCryptPasswordEncoder 빈을 정의합니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}