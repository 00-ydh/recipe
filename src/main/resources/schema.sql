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