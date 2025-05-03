# 8아요 - 실시간 경매 서비스

<br>

## 1. 프로젝트 소개

![Image](https://github.com/user-attachments/assets/f2096aa1-19ac-494c-83d4-8d3564a8e586)

<div align="center">

**8아요(Palayo)는 Spring 기반으로 구현한 실시간 경매 서비스**입니다.

사용자는 경매를 생성하거나 입찰에 참여할 수 있으며,  
실시간 알림을 통해  경쟁자의 입찰에 즉시 반응하고 빠르게 재입찰할 수 있습니다.

트래픽이 몰리는 상황에서도 입찰 및 낙찰 처리, 포인트 정산 등의  
핵심 기능이  안정적으로 작동하며, 실시간성과 정확성을 갖춘 경매 환경을 제공합니다.

</div>

<br>

## 2. 팀원 소개
![Image](https://github.com/user-attachments/assets/6f529fa6-8b6e-4e0a-941a-cf31bd02420c)

<br>

## 3. 기술 스택

### 언어 및 프레임워크  
<p>
  <img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/SPRING-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
</p>

### 데이터 베이스  
<p>
  <img src="https://img.shields.io/badge/MYSQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/REDIS-DC382D?style=for-the-badge&logo=redis&logoColor=white">
</p>

### 사용자 인증 및 보안  
<p>
  <img src="https://img.shields.io/badge/SPRING_SECURITY-4CAF50?style=for-the-badge&logo=springsecurity&logoColor=white">
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white">
</p>

### 검색 및 로그 분석  
<p>
  <img src="https://img.shields.io/badge/ELASTICSEARCH-005571?style=for-the-badge&logo=elasticsearch&logoColor=white">
  <img src="https://img.shields.io/badge/KIBANA-E8478B?style=for-the-badge&logo=kibana&logoColor=white">
  <img src="https://img.shields.io/badge/LOKI-0B5FFF?style=for-the-badge&logo=grafana&logoColor=white">
</p>

### 배포 및 형상 관리  
<p>
  <img src="https://img.shields.io/badge/DOCKER-2496ED?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/GITHUB_ACTIONS-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">
  <img src="https://img.shields.io/badge/GITHUB-181717?style=for-the-badge&logo=github&logoColor=white">
</p>

### 메시징 및 비동기 처리  
<p>
  <img src="https://img.shields.io/badge/WEBSOCKET-00C7E6?style=for-the-badge&logo=socketdotio&logoColor=white">
  <img src="https://img.shields.io/badge/RABBITMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white">
  <img src="https://img.shields.io/badge/QUARTZ-6E4C9F?style=for-the-badge&logo=clockify&logoColor=white">
  <img src="https://img.shields.io/badge/LUA-000080?style=for-the-badge&logo=lua&logoColor=white">
</p>

### 테스트 및 모니터링  
<p>
  <img src="https://img.shields.io/badge/PROMETHEUS-E6522C?style=for-the-badge&logo=prometheus&logoColor=white">
  <img src="https://img.shields.io/badge/GRAFANA-F46800?style=for-the-badge&logo=grafana&logoColor=white">
  <img src="https://img.shields.io/badge/POSTMAN-FF6C37?style=for-the-badge&logo=postman&logoColor=white">
  <img src="https://img.shields.io/badge/JMETER-D22128?style=for-the-badge&logo=apachejmeter&logoColor=white">
  <img src="https://img.shields.io/badge/JACOCO-DAF455?style=for-the-badge&logo=codecov&logoColor=white">
</p>

### 클라우드 인프라 환경  
<p>
  <img src="https://img.shields.io/badge/S3-FFB74D?style=for-the-badge&logo=cloudflare&logoColor=white">
  <img src="https://img.shields.io/badge/CloudFront-FFD54F?style=for-the-badge&logo=cloudflare&logoColor=white">
  <img src="https://img.shields.io/badge/EC2-FF8A65?style=for-the-badge&logo=cloudflare&logoColor=white">
  <img src="https://img.shields.io/badge/Route_53-FFF176?style=for-the-badge&logo=cloudflare&logoColor=white">
  <img src="https://img.shields.io/badge/Elastic_Beanstalk-AED581?style=for-the-badge&logo=cloudflare&logoColor=white">
</p>

<br>

## 4. 주요 기능  

#### 👤 **회원 관리**  
- 회원가입, 로그인, 정보 수정, 탈퇴  
- 비밀번호 변경, 닉네임 수정  
- JWT 기반 인증 및 보안 처리  

#### 🎁 **상품 관리**  
- 상품 등록, 수정, 삭제  
- AWS S3 이미지 업로드 및 URL 처리  
- 판매자 상품 목록 및 상세 조회  

#### 🔎 **검색 기능**  
- Elasticsearch 기반 키워드/카테고리 검색  
- 빠르고 정확한 검색 결과 제공  

#### 🔥 **경매 시스템**  
- 경매 등록, 조회, 삭제 (예약/실시간 경매 지원)  
- 경매 상태 자동 전환 (READY, ACTIVE, SUCCESS, FAILED)  
- 즉시 낙찰 조건 만족 시 자동 낙찰  
- 낙찰 후 판매자 정산 및 실패자 보증금 환불  
- Redisson Lock으로 입찰 동시성 제어  
- WebSocket으로 실시간 입찰 내역 반영  

#### 💰 **포인트 및 결제 시스템**  
- Toss API 결제 → 동일 금액 포인트 지급  
- 낙찰 시 입찰가 - 보증금만큼 추가 차감  
- Redis + Lua로 안전한 포인트 차감  
- 포인트/결제 내역 조회  

#### 🪙 **보증금 처리**  
- 경매당 최초 1회만 보증금 차감  
- 입찰 시 시작가의 10%를 보증금으로 차감  
- 보증금 이력 목록 및 상세 내역 조회  

#### 🔔 **실시간 알림**  
- 경매 시작/마감 5분 전 알림  
- 입찰 역전, 낙찰, 유찰 알림  
- 찜한 경매 이벤트 알림  

#### ❤️ **찜하기 기능**  
- 경매 찜 등록 및 해제  
- Redis 캐싱 기반 찜 목록/상세 조회

<br>

## 5. 아키텍처

#### v1.0 단일 서버 
![Image](https://github.com/user-attachments/assets/5da180d4-e06a-41f8-9a71-b4683b7f61cb)

<br>

#### v1.1 오토 스케일링 
![Image](https://github.com/user-attachments/assets/f8784f58-890c-444a-b1d0-660d0c8051f2)

<br>

## 6. ERD

![Image](https://github.com/user-attachments/assets/be0025e8-6c48-447d-a4e7-b5d7769746f0)

<br>

## 7. 와이어프레임
**Figma Url: https://iii.ad/ec6b23**

![Image](https://github.com/user-attachments/assets/6194f512-d3b2-4f19-9253-7fc4c47e83d3)

<br>

## 8. 기술적 의사결정

아래의 링크를 클릭하여 확인하실 수 있습니다.

[<strong>기술적 의사결정 바로가기</strong>](https://www.notion.so/1e7bed41c5bf8027bdd2cffe4c125add?pvs=4)

<br>

## 9. 기술 적용 및 성능 개선 사례

작성 예정

<br>

## 10. 트러블 슈팅

작성 예정

