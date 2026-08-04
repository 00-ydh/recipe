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

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(int followingId, int followerId) {
        jdbcTemplate.update("INSERT INTO follow (following_id, follower_id) VALUES  (?, ?)",
                followingId,
                followerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(int followingId, int followerId) {
        jdbcTemplate.update("DELETE FROM follow WHERE following_id = ? AND follower_id = ?", followingId, followerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FollowDto> findFollowingById(int followingId) {
        return jdbcTemplate.query("SELECT * FROM follow WHERE following_id = ?", memberDtoRowMapper, followingId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FollowDto> findFollowerById(int followerId) {
        return jdbcTemplate.query("SELECT * FROM follow WHERE follower_id = ?", memberDtoRowMapper, followerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FollowDto findFollowById(int followingId, int followerId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM follow WHERE following_id = ? AND follower_id = ?", memberDtoRowMapper, followingId, followerId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countFollowers(int memberId) {
        String sql = "SELECT COUNT(*) FROM follow WHERE follower_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, memberId);
    }
}
