package net.likelion.bebc25.recipe.follow.repository;

import net.likelion.bebc25.recipe.follow.dto.FollowDto;
import net.likelion.bebc25.recipe.member.dto.MemberDto;
import net.likelion.bebc25.recipe.post.dto.PostDto;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class jdbcTemplateFollowRepository implements FollowRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * 생성자를 통해 의존하는 JdbcTemplate을 주입받습니다.
     * @param jdbcTemplate 스프링 빈으로 등록된 JdbcTemplate 객체
     */
    public jdbcTemplateFollowRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<PostDto> postDtoRowMapper = (ResultSet rs, int rowNum) -> {
        return PostDto.builder()
                .id(rs.getInt("id"))
                .memberId(rs.getInt("member_id"))
                .mainImage(rs.getString("main_image"))
                .title(rs.getString("title"))
                .categoryId(rs.getInt("category_id"))
                .content(rs.getString("content"))
                .viewCount(rs.getInt("view_count"))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .postType(rs.getInt("post_type"))
                // 컬럼 존재 여부를 확인 후 안전하게 매핑 (없으면 null)
                .categoryName(hasColumn(rs, "category_name") ? rs.getString("category_name") : null)
                // 쿼리에서 'name' 또는 'member_name' 별칭(alias)을 다르게 쓸 수 있으므로 둘 다 대응
                .memberName(hasColumn(rs, "member_name") ? rs.getString("member_name") :
                        (hasColumn(rs, "name") ? rs.getString("name") : null))
                .likeCount(hasColumn(rs, "like_count") ? rs.getInt("like_count") : 0)
                .build();
    };

    // 2. 클래스 하단에 추가할 도우미 메서드 (컬럼 존재 여부 체크)
    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                if (columnName.equalsIgnoreCase(metaData.getColumnLabel(i))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }
    /**
     * 데이터베이스 ResultSet 데이터를 FollowDto 객체로 변환하는 맵퍼 정의입니다.
     */
    private final RowMapper<FollowDto> memberDtoRowMapper = (ResultSet rs, int rowNum) -> {
        return FollowDto.builder()
                .id(rs.getInt("id"))
                .followingId(rs.getInt("following_id"))
                .followerId(rs.getInt("follower_id"))
                .build();
    };


    @Override
    public void save(int followingId, int followerId) {
        jdbcTemplate.update("INSERT INTO follow (following_id, follower_id) VALUES  (?, ?)",
                followingId,
                followerId);
    }

    @Override
    public void delete(int followingId, int followerId) {
        jdbcTemplate.update("DELETE FROM follow WHERE following_id = ? AND follower_id = ?", followingId, followerId);
    }

    @Override
    public List<FollowDto> findFollowingById(int followingId) {
        return jdbcTemplate.query("SELECT * FROM follow WHERE following_id = ?", memberDtoRowMapper, followingId);
    }

    @Override
    public List<FollowDto> findFollowerById(int followerId) {
        return jdbcTemplate.query("SELECT * FROM follow WHERE follower_id = ?", memberDtoRowMapper, followerId);
    }

    @Override
    public FollowDto findFollowById(int followingId, int followerId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM follow WHERE following_id = ? AND follower_id = ?", memberDtoRowMapper, followingId, followerId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }



    @Override
    public List<PostDto> findFollowingMemberRecipes(int followingId) {
        String sql = "SELECT p.*, c.category_name, m.name AS member_name " +
                "FROM post p " +
                "LEFT JOIN category c ON p.category_id = c.id " +
                "LEFT JOIN member m ON p.member_id = m.id " +
                "WHERE p.post_type = 1 " +
                "AND p.member_id IN (" +
                "SELECT follower_id FROM follow WHERE following_id = ?)" +
                "ORDER BY p.created_at DESC";
        return jdbcTemplate.query(sql, postDtoRowMapper, followingId);
    }

    @Override
    public int countFollowers(int memberId) {
        String sql = "SELECT COUNT(*) FROM follow WHERE follower_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, memberId);
    }
}
