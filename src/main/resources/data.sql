
-- 카테고리
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


-- 레시피 게시글 (post_type = 1, category_id: 1=한식 2=양식 3=중식 4=일식)
INSERT INTO post (member_id, category_id, title, content, view_count, like_count, post_type)
VALUES
    (1, 1, '집에서 만드는 김치찌개', '돼지고기와 잘 익은 김치로 끓이는 칼칼한 김치찌개 레시피입니다.', 0, 0, 1),
    (2, 1, '간단 된장찌개', '두부와 애호박을 넣은 구수한 된장찌개입니다.', 0, 0, 1),
    (3, 2, '크림 파스타', '생크림으로 만드는 부드러운 크림 파스타 레시피입니다.', 0, 0, 1),
    (4, 2, '함박스테이크', '촉촉한 수제 함박스테이크에 데미글라스 소스를 곁들인 레시피입니다.', 0, 0, 1),
    (5, 3, '마파두부', '두반장으로 만드는 얼큰한 마파두부 레시피입니다.', 0, 0, 1),
    (1, 4, '연어 덮밥', '신선한 연어와 아보카도로 만드는 연어 덮밥 레시피입니다.', 0, 0, 1);

-- 꿀팁 게시글 (post_type = 2, category_id NULL 가능)
INSERT INTO post (member_id, category_id, title, content, view_count, like_count, post_type)
VALUES
    (1, NULL, '파스타 면 안 불게 하는 법', '파스타를 삶을 때 소금을 넉넉히 넣고, 소스에 버무리기 직전에 건져내면 면이 불지 않아요.', 15, 5, 2),
    (2, NULL, '마늘 빠르게 까는 꿀팁', '마늘을 칼 옆면으로 한 번 눌러주면 껍질이 쉽게 벗겨집니다.', 56, 45, 2),
    (3, NULL, '계란 신선도 확인하는 방법', '물에 넣었을 때 가라앉으면 신선한 계란, 떠오르면 오래된 계란이에요.', 23, 12, 2),
    (4, NULL, '양파 눈물 안 흘리는 법', '양파를 냉장고에 30분 넣었다가 썰면 눈물이 훨씬 덜 나요.', 36, 21, 2);