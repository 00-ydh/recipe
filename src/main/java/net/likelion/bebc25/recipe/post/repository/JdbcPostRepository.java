package net.likelion.bebc25.recipe.post.repository;

import net.likelion.bebc25.recipe.post.dto.PostDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcPostRepository implements  PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }
    private final RowMapper<PostDto> postDtoRowMapper = (ResultSet rs, int rowNum) ->{
        return PostDto.builder()
                .id(rs.getInt("id"))
                .memberId(rs.getInt("member_id"))
                .mainImage(rs.getString("main_image"))
                .title(rs.getString("title"))
                .categoryId(rs.getInt("category_id"))
                .content(rs.getString("content"))
                .viewCount(rs.getInt("view_count"))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .postType(rs.getInt("post_type")).build();
    };

    @Override
    public List<PostDto> findAll() {
        return List.of();
    }

    @Override
    public List<PostDto> findRecipePosts(int type) {
        return List.of();
    }

    @Override
    public List<PostDto> findTipPosts(int type) {
        return List.of();
    }

    @Override
    public PostDto findById(int id) {
        return null;
    }

    @Override
    public void save(PostDto post) {

    }

    @Override
    public void update(PostDto post) {

    }

    @Override
    public void deleteById(int id) {

    }
}
