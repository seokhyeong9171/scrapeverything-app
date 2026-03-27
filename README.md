# Scrap Everything - Android

웹서핑, SNS, 유튜브 등 휴대폰 사용 중 저장하고 싶은 페이지들을 카테고리별로 모아두는 안드로이드 앱입니다.

## 기술 스택

- **Language**: Kotlin 2.2.10
- **UI**: Jetpack Compose + Material 3
- **아키텍처**: MVVM, Single Activity
- **DI**: Hilt 2.59.2
- **네트워크**: Retrofit 2.11.0 + OkHttp 4.12.0
- **로컬 DB**: Room 2.7.1
- **네비게이션**: Jetpack Navigation Compose 2.8.5
- **이미지**: Coil 2.7.0
- **보안**: EncryptedSharedPreferences (AES256)
- **HTML 파싱**: Jsoup 1.18.3 (OG 메타데이터 추출)
- **AI 요약**: Groq API
- **광고**: Google Mobile Ads 24.1.0
- **업데이트**: Google Play In-App Update 2.1.0
- **비동기**: Kotlin Coroutines 1.9.0
- **Target SDK**: 35, **Min SDK**: 26

## 주요 기능

- 카테고리별 스크랩 관리 (CRUD)
- URL의 Open Graph 메타데이터 자동 추출 (제목, 설명, 이미지)
- AI 기반 URL 콘텐츠 요약 (Groq API)
- 외부 앱에서 공유하기로 스크랩 추가 (Share Intent)
- 딥링크 지원 (App Links + Custom Scheme)
- 데이터 백업/복원
- 이메일 인증 기반 회원가입
- JWT 자동 갱신 (OkHttp Authenticator)
- 다크 모드 / 라이트 모드 / 시스템 설정 테마
- 커서 기반 무한 스크롤
- 인앱 업데이트

## 화면 구성

| 화면 | 설명 |
|------|------|
| Splash | 초기 로딩 (토큰 확인) |
| Login | 로그인 |
| Register | 회원가입 (이메일 인증 포함) |
| CategoryList | 카테고리 목록 (홈 화면) |
| ScrapList | 카테고리별 스크랩 목록 |
| ScrapDetail | 스크랩 상세 (OG 프리뷰 포함) |
| ScrapAdd | 스크랩 추가 |
| ScrapAddFromShare | 외부 공유로 스크랩 추가 |
| ScrapEdit | 스크랩 수정 (카테고리 이동 가능) |
| MyPage | 회원정보, 테마 설정, 로그아웃, 탈퇴 |
| BackupRestore | 데이터 백업/복원 |
| Notice | 공지사항 |

## 실행 방법

### 사전 요구사항

- Android Studio
- JDK 17

### local.properties 설정

```properties
SERVER_URL=http://10.0.2.2:8080/
ADMOB_APP_ID=your_admob_app_id
ADMOB_BANNER_ID=your_admob_banner_id
GROQ_API_KEY=your_groq_api_key
STORE_FILE=scrapeverything-release.jks
STORE_PASSWORD=your_store_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

### 빌드

```bash
# 디버그 빌드
./gradlew assembleDebug

# 릴리즈 빌드
./gradlew assembleRelease
```

## 로컬 데이터베이스 (Room)

### category

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | Long | PK |
| uuid | String | 동기화/백업용 UUID |
| name | String | 카테고리명 |
| createdAt | Long | 생성 시각 |
| updatedAt | Long | 수정 시각 |

### scrap

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | Long | PK |
| uuid | String | 동기화/백업용 UUID |
| categoryId | Long | FK → category |
| title | String | 제목 |
| url | String | URL |
| summary | String? | AI 요약 |
| description | String? | 사용자 설명 |
| ogTitle | String? | OG 제목 |
| ogDescription | String? | OG 설명 |
| ogImageUrl | String? | OG 이미지 URL |
| createdAt | Long | 생성 시각 |
| updatedAt | Long | 수정 시각 |

## 네트워크 / 인증

### 토큰 관리

- **TokenStorage**: EncryptedSharedPreferences로 JWT, Refresh Token 암호화 저장
- **AuthInterceptor**: 모든 인증 요청에 `Authorization: Bearer {JWT}` 헤더 자동 추가
- **RefreshInterceptor**: 갱신 요청에 `Cookie: refresh_token={token}` 자동 추가
- **TokenAuthenticator**: 401 응답 시 자동으로 토큰 갱신 후 재시도
- **SessionManager**: 토큰 갱신 실패 시 `sessionExpiredEvent` 발행 → 로그인 화면 이동

### API 인터페이스

| 인터페이스 | 엔드포인트 | 설명 |
|-----------|-----------|------|
| AuthApi | `/api/v1/auth/*` | 로그인, 회원가입, 토큰 갱신, 로그아웃, 탈퇴 |
| MemberApi | `/api/v1/members/*` | 회원정보 조회/수정, 닉네임 중복 확인 |
| BackupApi | `/api/v1/backup/*` | 백업 업로드/목록/복원 |
| NoticeApi | `/api/v1/notice/*` | 공지사항 |
| GroqApi | `chat/completions` | AI 요약 (별도 Retrofit 인스턴스) |

## 딥링크

| 타입 | URL 패턴 |
|------|----------|
| App Links | `https://{SERVER_HOST}/share?url=...&title=...&desc=...` |
| Custom Scheme | `scrapeverything://share?url=...&title=...&desc=...` |
| Share Intent | `ACTION_SEND` (text/plain) |

## 프로젝트 구조

```
app/src/main/java/com/scrapeverything/app/
├── MainActivity.kt                # Single Activity (Compose)
├── ScrapEverythingApplication.kt  # @HiltAndroidApp
│
├── data/
│   ├── api/                      # Retrofit 인터페이스
│   │   ├── AuthApi               # 인증 API
│   │   ├── MemberApi             # 회원 API
│   │   ├── BackupApi             # 백업 API
│   │   ├── NoticeApi             # 공지사항 API
│   │   └── GroqApi               # AI 요약 API
│   ├── model/
│   │   ├── request/              # 요청 DTO
│   │   ├── response/             # 응답 DTO
│   │   └── groq/                 # Groq API 모델
│   ├── local/
│   │   ├── TokenStorage          # 암호화 토큰 저장
│   │   ├── ThemePreferences      # 테마 설정 저장
│   │   ├── SharedUrlHolder       # 공유 인텐트 데이터
│   │   ├── OpenGraphFetcher      # OG 메타데이터 추출
│   │   ├── AiSummarizer          # Groq 기반 AI 요약
│   │   └── db/
│   │       ├── AppDatabase       # Room DB (v2, 마이그레이션 포함)
│   │       ├── entity/           # CategoryEntity, ScrapEntity
│   │       └── dao/              # CategoryDao, ScrapDao
│   └── repository/
│       ├── AuthRepository        # 인증 로직
│       ├── CategoryRepository    # 카테고리 CRUD
│       ├── ScrapRepository       # 스크랩 CRUD + OG 추출
│       ├── MemberRepository      # 회원정보
│       ├── BackupRepository      # 백업/복원 (UUID 기반 매핑)
│       └── NoticeRepository      # 공지사항
│
├── network/
│   ├── ApiResult                 # Sealed class (Success/Error/NetworkError)
│   ├── AuthInterceptor           # JWT 헤더 추가
│   ├── RefreshInterceptor        # Refresh 쿠키 추가
│   ├── TokenAuthenticator        # 401 시 자동 갱신
│   ├── RefreshApiProvider        # 갱신 전용 Retrofit
│   ├── ErrorParser               # 에러 응답 파싱
│   └── SessionManager            # 세션 만료 이벤트
│
├── di/
│   ├── NetworkModule             # Retrofit + OkHttp 설정
│   ├── DatabaseModule            # Room DB 설정
│   ├── RepositoryModule          # Repository 바인딩
│   └── GroqModule                # Groq API 설정
│
├── ui/
│   ├── navigation/
│   │   ├── Route                 # Sealed class (화면 라우트)
│   │   └── NavGraph              # NavHost 설정 (fade 애니메이션)
│   ├── splash/                   # 스플래시 화면
│   ├── auth/                     # 로그인, 회원가입
│   ├── category/                 # 카테고리 목록, 다이얼로그
│   ├── scrap/                    # 스크랩 목록/상세/추가/수정/공유추가
│   ├── member/                   # 마이페이지
│   ├── backup/                   # 백업/복원
│   ├── notice/                   # 공지사항
│   ├── component/                # 공통 컴포넌트
│   │   ├── AdBanner              # Google 광고 배너
│   │   ├── OgPreviewCard         # OG 프리뷰 카드
│   │   ├── LoadingIndicator      # 로딩 인디케이터
│   │   ├── EmptyView             # 빈 상태 뷰
│   │   ├── ErrorView             # 에러 뷰
│   │   ├── ConfirmDialog         # 확인 다이얼로그
│   │   └── UpdateDialog          # 인앱 업데이트 다이얼로그
│   └── theme/                    # Color, Theme, Typography
│
└── util/
    ├── ErrorMessages             # 에러 코드 → 한국어 메시지 매핑
    └── Validators                # 입력값 검증 (이메일, 비밀번호, 닉네임)
```
