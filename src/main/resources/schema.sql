DROP TABLE IF EXISTS follow;
DROP TABLE IF EXISTS good;
DROP TABLE IF EXISTS reply;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS member;

CREATE TABLE member (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        email VARCHAR(100) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        name VARCHAR(50) NOT NULL,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

# 게시글 등록에서 category null 조건문 안넣으면 오류남
CREATE TABLE category (
                          id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          category_name VARCHAR(50) NOT NULL
);

CREATE TABLE post (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      member_id INT,
                      category_id INT,
                      main_image VARCHAR(200) NULL,
                      title VARCHAR(200) NOT NULL,
                      content TEXT NOT NULL,
                      view_count INT NOT NULL DEFAULT 0,
                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                      post_type INT NOT NULL,
                      CONSTRAINT fk_post_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL,
                      CONSTRAINT fk_post_category_id FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);


CREATE TABLE reply (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       post_id INT NOT NULL,
                       member_id INT NOT NULL,
                       content TEXT NOT NULL,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT fk_reply_target_id FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                       CONSTRAINT fk_reply_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);


CREATE TABLE good (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      post_id INT NOT NULL,
                      member_id INT NOT NULL,
                      like_type INT NOT NULL,
                      CONSTRAINT fk_like_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                      CONSTRAINT fk_like_target FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE
);




CREATE TABLE follow (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        following_id INT,
                        follower_id INT,
                        CONSTRAINT fk_follow_following_id FOREIGN KEY (following_id) REFERENCES member(id) ON DELETE CASCADE,
                        CONSTRAINT fk_follow_follower_id FOREIGN KEY (follower_id) REFERENCES member(id) ON DELETE CASCADE
);


INSERT INTO category(id, category_name) VALUES (1,'한식');
INSERT INTO category(id, category_name) VALUES (2,'양식');
INSERT INTO category(id, category_name) VALUES (3,'중식');
INSERT INTO category(id, category_name) VALUES (4,'일식');



INSERT INTO member (email, password, name)
VALUES
    ('kim@example.com', '1234', '김철수'),
    ('lee@example.com', '1234', '이영희'),
    ('park@example.com', '1234', '박민수'),
    ('choi@example.com', '1234', '최지은'),
    ('jung@example.com', '1234', '정현우');
