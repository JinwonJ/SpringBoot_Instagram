package org.clonestudy.instagram.global.config;

import org.clonestudy.instagram.auth.jwt.JwtAuthFilter;
import org.clonestudy.instagram.auth.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ✅ JWT 기반 API용 Security 설정 (정석)
     * - CSRF 비활성화
     * - 세션 안씀(STATELESS)
     * - formLogin/httpBasic 비활성화 (기본 로그인/기본 비번 생성 방지)
     * - JwtAuthFilter 등록
     * - CORS 활성화
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider tokenProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                // ✅ 기본 로그인/Basic 끄기 (generated password 방지 & API 형태 유지)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // ✅ 세션 사용 안함
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ 권한 규칙
                .authorizeHttpRequests(auth -> auth
                        // 정적 업로드 파일
                        .requestMatchers("/uploads/**").permitAll()

                        // 인증 API는 공개
                        .requestMatchers("/api/auth/signup", "/api/auth/login", "/api/auth/refresh").permitAll()

                        // (선택) 댓글 목록은 공개하고 싶으면 GET만 공개
                        .requestMatchers(HttpMethod.GET, "/api/posts/*/comments").permitAll()

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // ✅ JWT 필터 적용 (반드시 UsernamePasswordAuthenticationFilter 앞에)
                .addFilterBefore(new JwtAuthFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 개발용 CORS (Next/React)
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
