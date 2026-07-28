# 1. 데이터베이스 ERD 및 테이블 정의서

## 목차
- [1. 데이터베이스 ERD 및 테이블 정의서](#1-데이터베이스-erd-및-테이블-정의서)
- [1.1 Mermaid 기반 ERD 다이어그램](#11-mermaid-기반-erd-다이어그램)
- [1.2 테이블 상세 명세서](#12-테이블-상세-명세서)
- [1.3 테이블 생성 DDL 스크립트](#13-테이블-생성-ddl-스크립트)

---

## 1.1 Mermaid 기반 ERD 다이어그램

```mermaid
erDiagram
    MEMBER ||--o{ POST : "작성 (ON DELETE SET NULL)"
    CATEGORY ||--o{ POST : "분류 (ON DELETE SET NULL)"
    
    POST ||--o{ REPLY : "댓글 포함 (ON DELETE CASCADE)"
    MEMBER ||--o{ REPLY : "댓글 작성 (ON DELETE CASCADE)"
    
    POST ||--o{ LIKE : "좋아요 포함 (ON DELETE CASCADE)"
    MEMBER ||--o{ LIKE : "좋아요 함 (ON DELETE CASCADE)"
    
    MEMBER ||--o{ FOLLOW : "팔로잉 관계 (ON DELETE CASCADE)"
    MEMBER ||--o{ FOLLOW : "팔로워 관계 (ON DELETE CASCADE)"
    
    
    MEMBER {
        int id PK
        varchar_100 email
        varchar_255 password
        varchar_50 name
        varchar_50 nickname
        varchar_20 phone
        int follower_count
        datetime created_at
    }

    POST {
        int id PK
        int member_id FK
        int target_id FK
        int category_id FK
        varchar_50 writer_name
        varchar_200 title
        text content
        int view_count
        datetime created_at
        int type
    }
    
    REPLY {
        int id PK
        int target_id FK
        int member_id FK
        text content
        datetime created_at
        int type
    }

    LIKE {
        int id PK
        int target_id FK
        int member_id FK
        int type
    }

    CATEGORY {
        int id PK
        varchar_50 category_name
    }
    
    FOLLOW {
        int id PK
        following_id FK
        follower_id FK
    }
    
```

---

## 1.2 테이블 상세 명세서

### 1.2.1 member (회원 테이블)
- id: INT, PRIMARY KEY, AUTO_INCREMENT (회원 고유 식별자)
- email: VARCHAR(100), NOT NULL (이메일 아이디)
- password: VARCHAR(255), NOT NULL (비밀번호)
- name: VARCHAR(50), NOT NULL (회원 이름)
- nickname: VARCHAR(50), NOT NULL (별명)
- phone: VARCHAR(20), NULL (전화번호)
- follower_count: INT (팔로우 수)
- created_at: DATETIME, DEFAULT CURRENT_TIMESTAMP (가입 일시)


### 1.2.2 post (게시글 테이블)
- id: INT, PRIMARY KEY, AUTO_INCREMENT (게시글 고유 식별자)
- member_id: INT, FOREIGN KEY, NULL (작성자 회원 식별자, 익명 글인 경우 NULL)
- target_id: INT FOREIGN KEY, NOT NULL (대상 게시판 식별자)
- category_id: INT FOREIGN KEY, NULL (카테고리 ID)
- writer_name: VARCHAR(50), NULL (작성자 이름)
- title: VARCHAR(200), NOT NULL (게시글 제목)
- content: TEXT, NOT NULL (게시글 본문)
- view_count: INT, DEFAULT 0 (조회수)
- created_at: DATETIME, DEFAULT CURRENT_TIMESTAMP (작성 일시)
- type: INT NOT NULL (1: 레시피 게시판, 2: 요리꿀팁 게시판)


### 1.2.3 reply (댓글 테이블)
- id: INT, PRIMARY KEY, AUTO_INCREMENT (댓글 고유 식별자)
- target_id: INT, FOREIGN KEY (대상 게시글 식별자)
- member_id: INT, FOREIGN KEY (댓글 작성자 식별자)
- content: TEXT, NOT NULL (댓글 내용)
- created_at: DATETIME, DEFAULT CURRENT_TIMESTAMP (작성 일시)
- type: INT NOT NULL (1: 레시피 댓글, 2: 요리꿀팁 댓글)


### 1.2.4 like (좋아요 테이블)
- id: INT, PRIMARY KEY, AUTO_INCREMENT (좋아요 고유 식별자)
- target_id: INT, FOREIGN KEY (대상 좋아요 식별자)
- member_id: INT, FOREIGN KEY (좋아요 작성자 식별자)
- type: INT NOT NULL (1: 레시피 좋아요, 2: 요리꿀팁 좋아요, 3: 레시피 스크랩)

### 1.2.5 category (카테고리 테이블)
- id: INT, PRIMARY KEY, AUTO_INCREMENT (카테고리 고유 식별자)
- category_name: VARCHAR(50) NOT NULL (카테고리 이름)

### 1.2.6 follow (팔로우 테이블)
- id: INT, PRIMARY KEY, AUTO_INCREMENT (팔로우 고유 식별자)
- following_id: INT, FOREIGN KEY (팔로잉 한 사람 ID) 
- follower_id: INT, FOREIGN KEY (팔로워 ID)
---

## 1.3 테이블 생성 DDL 스크립트

```sql
CREATE TABLE member (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    follower_count INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE post (
    id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NULL,
    target_id INT NOT NULL,
    category_id INT,
    writer_name VARCHAR(50) NULL,
    password VARCHAR(255) NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    view_count INT NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    type INT NOT NULL,
    CONSTRAINT fk_post_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL,
    CONSTRAINT fk_post_category_id FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);


CREATE TABLE reply (
    id INT AUTO_INCREMENT PRIMARY KEY,
    target_id INT NOT NULL,
    member_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    type INT NOT NULL,
    CONSTRAINT fk_reply_target_id FOREIGN KEY (target_id) REFERENCES post(id) ON DELETE CASCADE,
    CONSTRAINT fk_reply_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);


CREATE TABLE like (
    id INT AUTO_INCREMENT PRIMARY KEY,
    target_id INT NOT NULL,
    member_id INT NOT NULL,
    type INT NOT NULL,
    CONSTRAINT fk_like_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_like_target FOREIGN KEY (target_id) REFERENCES post(id) ON DELETE CASCADE
);


CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL
);


CREATE TABLE follow (
    id INT AUTO_INCREMENT PRIMARY KEY,
    following_id INT,
    follower_id INT,
    CONSTRAINT fk_follow_following_id FOREIGN KEY (following_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_follow_follower_id FOREIGN KEY (follower_id) REFERENCES member(id) ON DELETE CASCADE
);
```
