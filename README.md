# 📸 Instagram Clone (Spring Boot)

Spring Boot 기반으로 구현한 Instagram 클론 백엔드 프로젝트입니다.  
JWT 인증 구조와 Refresh Token, 기기별 세션 관리까지 포함한 실전형 백엔드 아키텍처를 구현했습니다.

---

## 🛠 Tech Stack

### Backend
• Java 17  
• Spring Boot 3.x  
• Spring Security 6  
• Spring Data JPA  
• PostgreSQL  
• JWT (Access Token + Refresh Token)

### Build & Tool
• Gradle  
• IntelliJ IDEA  
• Postman

---

## 📁 Project Structure

src  
└─ main  
　 ├─ java  
　 │  └─ org.clonestudy.instagram  
　 │  
　 │     • InstagramApplication.java  
　 │  
　 │     • global  
　 │       • common  
　 │         • ApiResponse.java  
　 │         • FileStorageService.java  
　 │       • config  
　 │         • SecurityConfig.java  
　 │         • WebConfig.java  
　 │  
　 │     • auth  
　 │       • controller  
　 │         • AuthController.java  
　 │       • service  
　 │         • AuthService.java  
　 │       • domain  
　 │         • RefreshToken.java  
　 │       • repo  
　 │         • RefreshTokenRepository.java  
　 │       • dto  
　 │         • AuthResponse.java  
　 │         • LoginRequest.java  
　 │         • RefreshRequest.java  
　 │         • LogoutRequest.java  
　 │         • SessionResponse.java  
　 │       • jwt  
　 │         • JwtTokenProvider.java  
　 │         • JwtAuthFilter.java  
　 │         • JwtProperties.java  
　 │         • AuthPrincipal.java  
　 │  
　 │     • user  
　 │       • controller  
　 │         • UserController.java  
　 │       • domain  
　 │         • User.java  
　 │       • repo  
　 │         • UserRepository.java  
　 │  
　 │     • follow  
　 │       • domain  
　 │         • Follow.java  
　 │       • repo  
　 │         • FollowRepository.java  
　 │  
　 │     • post  
　 │       • controller  
　 │         • PostController.java  
　 │         • UserFeedController.java  
　 │       • service  
　 │         • PostService.java  
　 │         • FeedService.java  
　 │       • domain  
　 │         • Post.java  
　 │         • PostImage.java  
　 │       • repo  
　 │         • PostRepository.java  
　 │       • dto  
　 │         • PostCreateResponse.java  
　 │         • PostDetailResponse.java  
　 │         • FeedItemResponse.java  
　 │         • FeedResponse.java  
　 │  
　 └─ resources  
　 　 • application.properties

---

## 🔐 Authentication & Security

### Access Token
• JWT 기반  
• DB 저장 ❌  
• Authorization 헤더 사용  
• 짧은 만료 시간 (기본 60분)

### Refresh Token
• JWT 기반  
• DB 저장 ✅ (SHA-256 해시)  
• 기기(deviceId) 단위 관리  
• Refresh 시 토큰 회전(Rotate) 적용

---

## 📱 Device-based Session Management

• 로그인 시 deviceId 전달  
• 하나의 기기 = 하나의 Refresh Token 세션  
• 지원 기능  
　• 내 로그인 기기 목록 조회  
　• 특정 기기 강제 로그아웃  
　• 모든 기기 로그아웃

---

## 🔑 API Overview

### Auth
• POST /api/auth/login  
• POST /api/auth/refresh  
• POST /api/auth/logout  
• GET /api/auth/sessions  
• DELETE /api/auth/sessions/{deviceId}  
• POST /api/auth/logout-all

### Post
• POST /api/posts  
• GET /api/posts/{postId}  
• GET /api/feed  
• GET /api/users/{userId}/posts

---

## 🖼 Image Upload

• multipart/form-data 방식  
• 게시글당 이미지 1~3장  
• 업로드 경로: /uploads/**  
• 브라우저 직접 접근 허용

---

## ⚙️ application.properties Example

server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/{name}  
spring.datasource.username={name}  
spring.datasource.password={password}

spring.jpa.hibernate.ddl-auto=update  
spring.jpa.open-in-view=false

app.jwt.secret=change-me-to-a-long-random-secret-change-me-1234567890-abcdef  
app.jwt.access-token-exp-min=60  
app.jwt.refresh-token-exp-days=14

app.upload.dir=./uploads  
app.upload.base-url=http://localhost:8080/uploads

---

## 🧪 API Testing

• Postman 기반 테스트  
• Authorization: Bearer {accessToken}  
• deviceId는 고정 UUID 사용 권장

---

## 🚀 Run

./gradlew bootRun  
또는 IntelliJ에서 InstagramApplication 실행

---

## 📌 Future Improvements

• 댓글 / 좋아요 기능  
• 알림(Notification) 시스템  
• 비공개 계정  
• Redis 기반 Refresh Token 관리  
• Next.js 프론트엔드 연동

---

## 👤 Author

Instagram Clone Study Project  
Backend: Spring Boot / Java 17  
