package net.likelion.bebc25.recipe.post.repository;

import net.likelion.bebc25.recipe.post.dto.PostDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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

    // 게시글 단건 조회 (id로 조회) - 좋아요 수 포함
    // 원래 코드: String sql = "SELECT * FROM post WHERE id = ?";
    //
    // 변경 이유: 좋아요 수(like_count)를 함께 가져오기 위해 like 테이블을 JOIN함
    // - LEFT JOIN member : 작성자 이름(member_name)을 가져오기 위함
    // - LEFT JOIN good : 해당 게시글의 좋아요 개수를 COUNT하기 위함 (테이블명: good)
    //   (LEFT JOIN 사용 → 좋아요가 0개여도 null이 아닌 0으로 반환됨)
    // - GROUP BY p.id : COUNT 집계를 위해 필요
    @Override
    public PostDto findById(int id) {
        String sql = "SELECT p.*, m.name AS member_name, COUNT(l.id) AS like_count " +
                     "FROM post p " +
                     "LEFT JOIN member m ON p.member_id = m.id " +
                     "LEFT JOIN good l ON p.id = l.post_id " +
                     "WHERE p.id = ? " +
                     "GROUP BY p.id";
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

    // 아이디로 게시글 목록 조회
    @Override
    public List<PostDto> findPostByUser(int memberId) {
        String sql = "SELECT post.* , category.category_name, member.name FROM post " +
                "LEFT JOIN category on post.category_id = category.id " +
                "LEFT JOIN member on post.member_id = member.id " +
                "WHERE post.member_id = ?";

        return jdbcTemplate.query(sql, postDtoRowMapper,memberId);
    }

    // 아이디로 레시피 게시글 목록 조회
    @Override
    public List<PostDto> findRecipePostByUser(int memberId) {
        String sql = "SELECT post.* , category.category_name, member.name FROM post " +
                "LEFT JOIN category on post.category_id = category.id " +
                "LEFT JOIN member on post.member_id = member.id " +
                "WHERE post.post_type = 1 AND post.member_id = ?";

        return jdbcTemplate.query(sql, postDtoRowMapper,memberId);
    }

    // 아이디로 꿀팁 게시글 목록 조회 [[새로추가됨!]]
    @Override
    public List<PostDto> findTipPostByUser(int memberId) {
        String sql = "SELECT p.*, m.name AS member_name FROM post p " +
                "LEFT JOIN member m ON p.member_id = m.id " +
                "WHERE p.post_type = 2 AND p.member_id = ?";

        return jdbcTemplate.query(sql, postDtoRowMapper, memberId);
    }

    // 레시피 게시글 조회 (post_type = 1)
    @Override
    public PostDto findRecipePostByPostId(int postId) {
        String sql = "SELECT post.* , category.category_name, member.name FROM post " +
                "LEFT JOIN category on post.category_id = category.id " +
                "LEFT JOIN member on post.member_id = member.id " +
                "WHERE post.post_type = 1 AND post.id = ?";
        return jdbcTemplate.queryForObject(sql, postDtoRowMapper, postId);
    }

    // 레시피 게시글 개수
    @Override
    public int recipePostCount() {
        String sql = "SELECT COUNT(*) FROM post WHERE post_type = 1";
        Integer  count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;
    }

    // 오늘뭐먹지 게시글에서 카테고리 아이디를 받아 랜덤으로 게시글 하나 가져오기
    @Override
    public PostDto findTodayPostByCategoryId(int categoryId) {
        String sql = "SELECT post.* , category.category_name, member.name FROM post " +
                "LEFT JOIN category on post.category_id = category.id " +
                "LEFT JOIN member on post.member_id = member.id " +
                "WHERE post.post_type = 1 " +
                "AND post.category_id = ? " +
                "ORDER BY RAND() LIMIT 1";

        List<PostDto> list =  jdbcTemplate.query(sql, postDtoRowMapper, categoryId);

        if(list.isEmpty()){
            return null;
        }
        return list.getFirst();
    }

    // 레시피 게시글 페이징
    // 페이지당 8개씩 보여줄 것임
    // 0, 8 넣고 Service에서 계산
    @Override
    public List<PostDto> findRecipePosts(String type, int offset, int limit) {
        String sql = "SELECT post.* , category.category_name, member.name FROM post " +
                "LEFT JOIN category on post.category_id = category.id " +
                "LEFT JOIN member on post.member_id = member.id " +
                "WHERE post.post_type = 1 ";

        // 조회순 / 최신순 / 등록순
        if("view".equals(type)) {
            sql += " ORDER BY post.view_count DESC ";
        }else if("latest".equals(type)) {
            sql += " ORDER BY post.created_at DESC ";
        }else{
            sql += " ORDER BY post.id ASC ";
        }
        // 페이징
        sql += " LIMIT ?, ?";
        return jdbcTemplate.query(sql, postDtoRowMapper, offset, limit);
    }

}

