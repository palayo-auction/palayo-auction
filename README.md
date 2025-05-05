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

#### 💰 **결제 및 포인트 처리**  
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

### 1. 입찰 동시성 제어

#### ❗ 문제 원인

- 동일 입찰가로 동시 요청 시, 락 없이 처리하면 입찰 중복 등록이나 무결성 오류 발생

#### 🛠️ 기술 도입

- Redisson 분산 락 적용: 애플리케이션 단에서 tryLock()으로 경매 ID 기준 동시 요청 제어
- DB Unique 제약조건 추가: DB 단에서 (auction_id, bid_price) 조합으로 중복 저장 방지
- 예외 핸들링 보강: 락 획득 실패나 제약조건 위반 시 사용자에게 오류 메시지 반환

#### 📈 입찰 처리 시스템 성능 비교

시나리오: 유저 5,000명이 동시에 경매 한 건에 2번씩 입찰 요청  

| 항목               | 기존 방식 (락 미적용)         | 개선 방식 (Redisson + DB 제약조건)     |
|--------------------|-----------------------------|-----------------------------|
| 평균 응답 시간      | 1,474ms                     | 3,814ms                     |
| 최대 응답 시간      | 9,674ms                     | 22,824ms                    |
| 오류율             | 99.98%                      | 99.99%                      |
| 처리량             | 613.0/sec                   | 265.2/sec                   |
| 입찰 처리 결과      | 2건 등록됨 (중복 발생)       | 1건만 등록됨 (정합성 유지) |

#### ✅ 성능 개선 요약

- **입찰 처리 결과**: 중복 입찰 2건 → 정확히 1건만 등록
- **오류율**: 99.99%는 시스템 오류가 아닌 중복 차단 로직에 따른 정상 처리
- **처리량**: 613/sec → 265/sec (약 56.8% 감소) 
- **응답 속도**: 1,474ms → 3,814ms (약 158.8% 증가) 

-> 처리 성능은 비교적 감소했지만, 충돌 상황에서도 정확히 1건만 처리됨

<br>

### 2. 포인트 차감 원자성 처리

#### ❗ 문제 원인  
- 동시에 여러 포인트 차감 요청이 들어올 경우, DB 트랜잭션만으로는 정합성이 깨질 수 있음  

#### 🛠️ 기술 도입  
- Redis Lua Script 도입: `잔액 확인 → 차감`을 하나의 원자 연산으로 처리  
- Java에서 `eval()`로 호출, Redis 값을 수동 복구하는 보상 로직 구현  
- 테스트 전 Redis 초기화를 위한 `/test/redis/init` API 생성

#### 📈 포인트 차감 시스템 성능 비교

**시나리오**: 사용자 10,000명이 동시에 포인트 차감 요청

| 항목 | 기존 방식 (DB 트랜잭션) | 개선 방식 (Redis Lua Script) |
|------|----------------|--------------------------|
| 평균 응답 시간 | 7,030ms | 16,094ms |
| 최대 응답 시간 | 21,643ms | 31,689ms |
| 오류율 | 46.21% | 23.02% |
| 처리량 | 291.7/sec | 191.2/sec |

#### ✅ 성능 개선 요약  
- **오류율**: 46.21% → 23.02% (약 50.2% 감소)  
- **평균 응답 시간**: 7,030ms → 16,094ms (약 128.9% 증가)  
- **처리량**: 291.7 요청/초 → 191.2 요청/초 (약 34.4% 감소)  

→ 처리 성능은 하락했지만, 중복 차감이 크게 줄고 정합성과 안정성이 향상됨

<br>

### 3. 검색 성능 개선 

#### ❗ 문제 원인

- MySQL은 복합 조건 검색 시 속도가 느리고, 데이터가 많아질수록 성능이 급격히 저하됨  
- 또한 LIKE 기반 검색은 유사어 검색이 불가능해 검색 정확도가 떨어짐

#### 🛠️ 기술 도입

- Elasticsearch 도입으로 유사도 검색 및 검색 속도 개선
- 형태소 분석 + 역색인 기반으로 빠르고 정확한 검색 지원

#### 📈 검색 성능 비교

시나리오: 데이터가 1만개 있을경우, 1만번 검색   

| 지표                     | Elasticsearch | RDB (MySQL) | 성능 향상률 |
|--------------------------|----------------|-------------|--------------|
| 평균 응답 시간 (Average) | 3ms            | 121ms       | **약 95.0% 향상** |
| 중앙값 (Median)         | 3ms            | 130ms       | **약 97.7% 향상** |
| 90% 지연선 (90% Line)    | 5ms            | 175ms       | **약 97.1% 향상** |
| 95% 지연선 (95% Line)    | 6ms            | 187ms       | **약 96.8% 향상** |
| 99% 지연선 (99% Line)    | 12ms           | 243ms       | **약 95.1% 향상** |
| 최소 응답 시간 (Min)     | 1ms            | 10ms        | **약 90.0% 향상** |
| 최대 응답 시간 (Max)     | 132ms          | 327ms       | **약 59.6% 향상** |

<p align="left">
  <img src="https://github.com/user-attachments/assets/8114cd6b-99b1-4f4a-87f6-8e576726cedc" width="565"/>
</p>

<br>

시나리오: 데이터가 10만개 있을 경우, 1만번 검색   

| 지표                     | Elasticsearch | RDB (MySQL) | 성능 향상률 |
|--------------------------|----------------|-------------|--------------|
| 평균 응답 시간 (Average) | 7ms            | 4,946ms     | **약 99.9% 향상** |
| 중앙값 (Median)         | 7ms            | 4,533ms     | **약 99.8% 향상** |
| 90% 지연선 (90% Line)    | 10ms           | 6,428ms     | **약 99.8% 향상** |
| 95% 지연선 (95% Line)    | 11ms           | 6,509ms     | **약 99.8% 향상** |
| 99% 지연선 (99% Line)    | 17ms           | 6,653ms     | **약 99.7% 향상** |
| 최소 응답 시간 (Min)     | 3ms            | 262ms       | **약 98.9% 향상** |
| 최대 응답 시간 (Max)     | 193ms          | 11,352ms    | **약 98.3% 향상** |

<p align="left">
  <img src="https://github.com/user-attachments/assets/0e8283c6-06f9-48f5-b984-161d12f9beda" width="565"/>
</p>

#### ✅ 성능 개선 요약

- **정확도 향상**: 기존 MySQL은 유사 단어 검색이 불가능해 검색 누락 발생  
- **검색 품질 개선**: 형태소 분석과 full-text query로 의미가 유사한 문장도 검색 가능
- **응답 시간 개선**:
  - 1만 건 기준 평균 응답 시간: 121ms → 3ms (약 95% 향상)
   - 10만 건 기준 평균 응답 시간: 4,946ms → 7ms (약 99.9% 향상)
- **대용량 대응력**: MySQL은 데이터 증가에 따라 성능이 급격히 저하되지만, Elasticsearch는 거의 선형 유지

<br>

### 4. 찜 목록 조회 성능 개선

#### ❗ 문제 원인

- 찜 목록을 MySQL에서 직접 조회하면서 조회 집중 시 DB 부하 증가
- 데이터가 많아질수록 응답 지연 및 대량 트래픽 대응 어려움
- 최대 응답 시간이 **100초(100,000ms)**까지 증가한 사례 발생

#### 🛠️ 기술 도입

- Redis 캐시 도입으로 조회 병목 해소 및 응답 속도 개선
- RDB 저장 시 Redis 캐시 동기화, 조회는 Redis 단독 처리
- Redis 구조: `dib::<userId>` → Set 형태, TTL 7일
- `createdAt` 기준 내림차순 정렬로 최신 찜 우선 조회

#### 📈 찜 목록 조회 성능 비교

시나리오: 사용자 5,000명이 동시에 찜 목록 조회 요청

| 항목             | 기존 방식 (MySQL) | 개선 방식 (Redis 캐시) |
|------------------|-------------------|--------------------------|
| 평균 응답 시간   | 60ms              | 5ms                      |
| 최대 응답 시간   | 592ms             | 21ms                     |
| 처리량           | 63.4/sec          | 83.3/sec                 |

<br>

**RDB, Redis 응답시간 속도 비교**

<p align="left">
  <img src="https://github.com/user-attachments/assets/4feaeaa1-ffa6-41e7-8453-2d7acd1c2a44" width="460"/>
</p>

#### ✅ 성능 개선 요약

- **평균 응답 시간**: 60ms → 5ms (약 91.7% 감소)
- **최대 응답 시간**: 592ms → 21ms (약 96.5% 감소)
- **처리량**: 63.4/sec → 83.3/sec (약 31.4% 증가)
- DB 부하 제거 및 캐시 중심 구조로 전환
- 체감 속도 및 대량 요청 안정성 향상

<br>

### 5. 경매 스케줄링 구조 개선

### 5-1. 경매 상태 변경 처리 개선

#### ❗ 문제 원인

- 경매는 진행 시간과 입찰 조건에 따라 상태가 자동 전환되는 구조임
- 기존에는 Spring Scheduler를 이용해 1초마다 전체 경매를 조회하고 상태 조건을 검사
- 경매 수가 늘어날수록 불필요한 반복 조회로 인해 서버 부하가 급격히 증가하고,
  실시간성이 중요한 경매 마감 시점의 신뢰성 저하 발생

#### 🛠️ 기술 도입

**1단계: Quartz Scheduler 도입**
- 경매 등록 시 `시작 Job`, `마감 Job`을 각각 Quartz에 등록
- `SimpleTrigger` 기반으로 millisecond 단위 실행 가능
- JDBC JobStore 설정으로 서버 재시작 시에도 Job 복원 가능

**2단계: Spring Scheduler 방식으로 전환**
- Quartz 제거 후, 배치 서버에서 60초마다 상태 일괄 검사
- Spring Scheduler 사용으로 구성 단순화 및 운영 편의성 확보

#### 📈 경매 상태 처리 성능 비교

| 항목               | Spring Scheduler 방식   | Quartz Scheduler 방식              |
|--------------------|-----------------------------|-------------------------------|
| Used Heap           | 95MB ~ 210MB                | 107MB ~ 190MB                 |
| GC 패턴             | 점진적 증가 후 GC           | 잦은 GC 유도 (급등락 반복)    |
| Live Threads        | 98                          | 98                            |
| Total Threads       | 147                         | 151                           |
| Peak Threads        | 100                         | 101                           |
| Loaded Classes      | 28,251 (Unloaded: 2)        | 28,198 (Unloaded: 0)          |
| CPU 사용률          | 평균 0.7%, 변동 적음        | 평균 0.7%, 변동 많음          |

#### ✅ 성능 개선 요약

- 전체 조회 방식 제거 → 경매 수와 무관하게 서버 부하 일정 유지
- Heap 사용량, Thread 수, CPU 사용률 등은 유사하거나 안정적
- Quartz는 millisecond 단위 정밀 제어가 가능하나, 운영 복잡도와 시스템 부하 관리 측면에서 불리
- Spring Scheduler 기반 배치 방식은 구성 단순화, 재실행 용이성, 운영 편의성에서 우수
- 60초 단위 오차는 수용 가능한 수준이며, 실시간성보다 시스템 안정성과 유지보수성을 우선
- 결과적으로, Quartz보다 실무에 적합한 선택지로 Spring Scheduler를 채택

<br>

### 5-2. 경매 알림 처리 구조 개선

#### ❗ 문제 원인

- 기존 구조는 Spring Scheduler를 사용하여 **1초마다 전체 알림 데이터를 조회하고 발송 대상 여부를 검사하는 방식**
- 경매 및 알림 수 증가에 따라 **불필요한 반복 조회로 서버 부하 및 DB 부하 증가**
- 1초 주기의 전체 조회로 인해 **알림 발송 시점의 정확성 및 실시간성 확보 어려움**

#### 🛠️ 기술 도입

**1단계: Quartz Scheduler + RabbitMQ 도입**

- 경매 등록 시, 시작/마감 시점별 알림을 개별 Quartz Job으로 등록하여 millisecond 단위 정밀한 알림 발송 가능
- Quartz Job 실행 시 RabbitMQ에 이벤트를 발행하고, Consumer들이 비동기로 알림 처리
- RabbitMQ를 통해 알림 전송 병목 해소 및 메시지 유실 방지 등 시스템 신뢰성 확보

**2단계: Spring Scheduler 방식으로 전환**

- Quartz 및 RabbitMQ 구조를 제거하고, Spring Scheduler를 통해 60초마다 알림 발송 대상을 일괄 조회 및 처리
- 스케줄링 로직을 단순화하여 운영과 유지보수의 편의성 향상, 장애 대응 및 모니터링 또한 용이해짐

#### 📈 알림 처리 구조 성능 비교

| 항목            | Spring Scheduler 방식        | Quartz + RabbitMQ 방식         |
|-----------------|------------------------------|-------------------------------|
| 구조 복잡도      | 낮음                         | 높음                          |
| 알림 정확성      | 보통 (최대 30~60초 오차)      | 높음 (ms 단위)                 |
| 운영 편의성      | 높음 (일괄 처리로 용이)       | 낮음 (Job 추적 복잡)            |
| 서버 부하 관리   | 예측 가능하며 단순화          | 설정 복잡하나 처리 효율 우수    |

#### ✅ 성능 개선 요약

- Quartz+RabbitMQ 방식은 정밀한 시간 관리가 가능했으나, 구조적 복잡성과 운영 관리 부담이 높음
- 알림 발송 최대 1분 지연은 실제 서비스에서 허용 가능한 수준으로 판단하여 운영 안정성을 최우선으로 고려함
- 최종적으로 Spring Scheduler 기반의 1분 주기 일괄 처리 방식을 선택하여 구조를 단순화하고 유지보수성을 확보
