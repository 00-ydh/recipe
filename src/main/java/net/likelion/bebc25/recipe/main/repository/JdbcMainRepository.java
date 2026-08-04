package net.likelion.bebc25.recipe.main.repository;

import net.likelion.bebc25.recipe.main.dto.MainDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcMainRepository implements MainRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcMainRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<MainDto> mainDtoRowMapper = (ResultSet rs, int rowNum) -> {
        return MainDto.builder()
                .id(rs.getInt("id"))
                .memberId(rs.getInt("member_id"))
                .mainImage(rs.getString("main_image"))
                .title(rs.getString("title"))
                .categoryId(rs.getInt("category_id"))
                .viewCount(rs.getInt("view_count"))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .postType(rs.getInt("post_type"))

                // JOIN으로 가져온 컬럼 (post 테이블에 없는 값들)
                .categoryName(rs.getString("category_name"))  // category 테이블 JOIN
                .memberName(rs.getString("member_name"))      // member 테이블 JOIN
                .likeCount(rs.getInt("like_count"))           // good 테이블 COUNT
                .build();
    };

    // 명예의 전당: 좋아요 많은 레시피 TOP 3
    // - LEFT JOIN category : 카테고리 이름(category_name) 가져오려고
    // - LEFT JOIN member   : 작성자 이름(member_name) 가져오려고
    // - LEFT JOIN good     : 좋아요 수(like_count) 세려고 → COUNT(g.id)로 집계
    // - WHERE post_type=1  : 레시피 게시글만 필터링
    // - GROUP BY p.id      : COUNT 집계할 때 게시글 기준으로 묶어주려고
    // - ORDER BY like_count DESC LIMIT 3 : 좋아요 많은 순 상위 3개만
    @Override
    public List<MainDto> findTopRecipes() {
        // post(레시피) + category(카테고리명) + member(작성자명) + good(좋아요수) 조인
        // good 테이블 행 수를 COUNT해서 좋아요 수 계산, 좋아요 많은 순 상위 3개 반환
        String sql = "SELECT p.*, c.category_name, m.name AS member_name, COUNT(g.id) AS like_count " +
                     "FROM post p " +
                     "LEFT JOIN category c ON p.category_id = c.id " +
                     "LEFT JOIN member m   ON p.member_id = m.id " +
                     "LEFT JOIN good g     ON p.id = g.post_id " +
                     "WHERE p.post_type = 1 " +
                     "GROUP BY p.id " +
                     "ORDER BY like_count DESC " +
                     "LIMIT 3";
        return jdbcTemplate.query(sql, mainDtoRowMapper);
    }


    // 이달의 추천: 한 달 이내 좋아요 많은 레시피 TOP 10
    // - LEFT JOIN category, member, good : findTopRecipes()와 동일한 이유로 JOIN
    // - AND created_at >= DATE_SUB(NOW(), INTERVAL 1 MONTH) : 현재 기준 1달 이내 글만 필터링
    // - ORDER BY like_count DESC LIMIT 10 : 좋아요 많은 순 상위 10개만
    @Override
    public List<MainDto> findMonthlyTopRecipes() {
        // findTopRecipes()와 동일한 조인 구조
        // 단, 현재 날짜 기준 1달 이내(DATE_SUB) 등록된 레시피만 필터링 후 좋아요 많은 순 상위 10개 반환
        String sql = "SELECT p.*, c.category_name, m.name AS member_name, COUNT(g.id) AS like_count " +
                     "FROM post p " +
                     "LEFT JOIN category c ON p.category_id = c.id " +
                     "LEFT JOIN member m   ON p.member_id = m.id " +
                     "LEFT JOIN good g     ON p.id = g.post_id " +
                     "WHERE p.post_type = 1 " +
                     "AND p.created_at >= DATE_SUB(NOW(), INTERVAL 1 MONTH) " +
                     "GROUP BY p.id " +
                     "ORDER BY like_count DESC " +
                     "LIMIT 10";
        return jdbcTemplate.query(sql, mainDtoRowMapper);
    }

    // 최신 요리 꿀팁: 최신순 6개
    // - LEFT JOIN member : 작성자 이름(member_name) 가져오려고
    // - LEFT JOIN good   : 좋아요 수 세려고 (꿀팁에도 좋아요 기능 있으므로)
    // - NULL AS category_name : 꿀팁은 카테고리가 없어서 null로 채움
    // - WHERE post_type=2 : 꿀팁 게시글만 필터링
    // - ORDER BY created_at DESC LIMIT 6 : 최신 등록순 상위 6개만
    @Override
    public List<MainDto> findLatestTipPosts() {
        // post(꿀팁) + member(작성자명) + good(좋아요수) 조인
        // 꿀팁은 카테고리가 없으므로 category_name은 NULL로 채움, 최신 등록순 상위 6개 반환
        String sql = "SELECT p.*, m.name AS member_name, NULL AS category_name, COUNT(g.id) AS like_count " +
                     "FROM post p " +
                     "LEFT JOIN member m ON p.member_id = m.id " +
                     "LEFT JOIN good g   ON p.id = g.post_id " +
                     "WHERE p.post_type = 2 " +
                     "GROUP BY p.id " +
                     "ORDER BY p.created_at DESC " +
                     "LIMIT 6";
        return jdbcTemplate.query(sql, mainDtoRowMapper);
    }


// 밥풀루언서 rowmapper 따로 , 여기부터 시작!!!!!!!

    // 밥플루언서용 RowMapper (member + follow 테이블만 읽음 )
    private final RowMapper<MainDto> memberRowMapper = (ResultSet rs, int rowNum) -> {
        return MainDto.builder()
                .id(rs.getInt("id"))
                .memberName(rs.getString("member_name"))   // 멤버 이름
                .followerCount(rs.getInt("follower_count")) // 팔로워 수
                .build();
    };

    // 밥플루언서: 팔로워 수 많은 멤버 TOP 5
    // - LEFT JOIN follow : 각 멤버를 팔로우한 사람 수(follower_count) 세려고 → COUNT(f.id)로 집계
    // - GROUP BY m.id    : 멤버 기준으로 팔로워 수 묶어주려고
    // - ORDER BY follower_count DESC LIMIT 5 : 팔로워 많은 순 상위 5명만
    @Override
    public List<MainDto> findTopMember() {
        // member + follow 조인 (follow 테이블에서 나를 팔로우한 사람 수를 COUNT)
        // 팔로워 많은 순 상위 5명 반환 (memberRowMapper 사용 - mainDtoRowMapper와 다름 주의!)
        String sql = "SELECT m.id, m.name AS member_name, COUNT(f.id) AS follower_count " +
                "FROM member m " +
                "LEFT JOIN follow f ON m.id = f.follower_id " +
                "GROUP BY m.id " +
                "ORDER BY follower_count DESC " +
                "LIMIT 5";
        return jdbcTemplate.query(sql, memberRowMapper);
    }


}