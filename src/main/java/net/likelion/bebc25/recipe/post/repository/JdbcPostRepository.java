package net.likelion.bebc25.recipe.post.repository;

import net.likelion.bebc25.recipe.post.dto.PostDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcPostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPostRepository(JdbcTemplate jdbcTemplate) {
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
                // [추가] 카테고리 이름
                .categoryName(rs.getString("category_name"))
                // [추가] member테이블에서 name 컬럼
                .memberName(rs.getString("name"))
                .build();
    };

    // 게시글 전체 조회 (레시피 + 꿀팁 모두)
    @Override
    public List<PostDto> findAll() {
        String sql = "SELECT * FROM post";
        return jdbcTemplate.query(sql, postDtoRowMapper);
    }

    // 레시피 게시글 조회 (post_type = 1)
    @Override
    public List<PostDto> findRecipePosts() {
        String sql = "SELECT post.* , category.category_name, member.name FROM post " +
                "LEFT JOIN category on post.category_id = category.id " +
                "LEFT JOIN member on post.member_id = member.id " +
                "WHERE post.post_type = 1";
        return jdbcTemplate.query(sql, postDtoRowMapper);
    }


    // 꿀팁 게시글 전체 조회 (post_type = 2)
    @Override
    public List<PostDto> findTipPosts() {
        String sql = "SELECT p.*, m.name AS member_name FROM post p LEFT JOIN member m ON p.member_id = m.id WHERE p.post_type = 2";
        return jdbcTemplate.query(sql, postDtoRowMapper);
    }

    // 게시글 단건 조회 (id로 조회)
    @Override
    public PostDto findById(int id) {
        String sql = "SELECT * FROM post WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, postDtoRowMapper, id);
    }

    // 게시글 등록
    //  post.getCategoryId() == 0 ? null : post.getCategoryId() - 오류
    @Override
    public void save(PostDto post) {
        String sql = "INSERT INTO post (member_id, category_id, main_image, title, content, post_type) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                post.getMemberId(),
                post.getCategoryId() == 0 ? null : post.getCategoryId(),
                post.getMainImage(),
                post.getTitle(),
                post.getContent(),
                post.getPostType());
    }

    // 게시글 수정
    @Override
    public void update(PostDto post) {
        String sql = "UPDATE post SET title = ?, content = ?, main_image = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                post.getTitle(),
                post.getContent(),
                post.getMainImage(),
                post.getId());
    }

    // 게시글 삭제
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM post WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}

