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

INSERT INTO post
(member_id, category_id, main_image, title, content, view_count, post_type)
VALUES
    (1, 1, NULL, '김치찌개 만드는 법',
     '돼지고기와 김치를 이용한 기본 김치찌개 레시피입니다.',
     0, 1);

INSERT INTO post
(member_id, category_id, main_image, title, content, view_count, post_type)
VALUES
    (2, 2, NULL, '짜장면 만들기',
     '춘장과 돼지고기를 사용한 집에서 만드는 짜장면입니다.',
     0, 1);

INSERT INTO post
(member_id, category_id, main_image, title, content, view_count, post_type)
VALUES
    (3, 3, NULL, '초밥 만들기',
     '신선한 생선과 초밥용 밥으로 만드는 초밥 레시피입니다.',
     0, 1);

INSERT INTO post
(member_id, category_id, main_image, title, content, view_count, post_type)
VALUES
    (1, 1, NULL, '된장찌개 레시피',
     '구수한 된장과 두부, 애호박을 넣어 만드는 된장찌개입니다.',
     0, 1);

INSERT INTO post
(member_id, category_id, main_image, title, content, view_count, post_type)
VALUES
    (2, 2, NULL, '마파두부 만들기',
     '두반장과 다진 돼지고기를 활용한 매콤한 마파두부입니다.',
     0, 1);