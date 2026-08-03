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
    // - post 테이블 기준으로 category, member, good 테이블 LEFT JOIN
    // - post_type = 1 (레시피만)
    // - good 테이블의 행 수를 세서 좋아요 수(like_count) 계산
    // - 좋아요 수 내림차순 정렬 후 3개만 가져옴
    @Override
    public List<MainDto> findTopRecipes() {
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


    // 밥플루언서: 팔로워 많은 멤버 TOP 5
    // TODO: 팔로우 기능 구현 후 SQL 작성
    @Override
    public List<MainDto> findTopMember() {
        return List.of();
    }



    

    // 이달의 추천: 한 달 이내 좋아요 많은 레시피 TOP 10
    // - 위 쿼리와 동일하지만 날짜 조건 추가
    // - created_at 이 현재로부터 1달 이내인 것만 필터링
    // - 좋아요 수 내림차순 정렬 후 10개만 가져옴
    @Override
    public List<MainDto> findMonthlyTopRecipes() {
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
    // - post_type = 2 (꿀팁만)
    // - member 테이블 JOIN 해서 작성자 이름 가져옴
    // - 작성일 내림차순 정렬 후 6개만 가져옴
    @Override
    public List<MainDto> findLatestTipPosts() {
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



}