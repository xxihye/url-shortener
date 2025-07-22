# 단축 URL 서비스 프로젝트

본 프로젝트는 JWT 기반 인증 및 인가가 적용된 단축 URL 서비스입니다.  
- Auth-service: 회원가입, 로그인, JWT 토큰 발급 및 재발급, 관리자 MFA 지원 예정
- URL-service: 단축 URL 생성, 조회, 리다이렉트 기능 제공

### 주요 기능
- JWT Access / Refresh 토큰을 통한 인증 및 권한 관리
- 회원가입, 로그인, 토큰 재발급
- 단축 URL 생성 및 조회, 리다이렉트 지원
- Spring Security 기반 필터 및 인증 처리
- Docker Compose를 통한 환경 구성
- 관리자 및 일반 사용자 권한 분리 (추후 MFA 도입 예정)

### 기술 스택
- Spring boot 3.4
- Java 17
- MySQL 8.3 (Docker)
- Spring Security, JPA(Hibernate)
- JWT
- Docker

서비스 간 MSA 아키텍처로 분리되어 있으며, 공통 secret 키는 별도 관리됩니다.  
Spring Security 필터를 통해 JWT 토큰 인증을 수행하며 SecurityContextHolder에 인증 정보를 유지합니다.  
토큰 관리는 DB에 Refresh Token을 저장합니다.  
향후 관리자 MFA 및 OAuth 연동 등의 기능을 확장할 계획입니다.
