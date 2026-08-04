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
    //
    private static final String BASE_SELECT_SQL =
            "SELECT p.id, p.member_id, p.category_id, p.main_image, p.title, p.content, p.view_count, p.created_at, p.post_type, m.name AS author " +
                    "FROM post p LEFT JOIN member m ON p.member_id = m.id";

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
        // 등록순 내림차순
        String sql = "SELECT p.*, m.name AS member_name " +
                     "FROM post p " +
                     "LEFT JOIN member m ON p.member_id = m.id " +
                     "WHERE p.post_type = 2 " +
                     "ORDER BY p.created_at DESC";
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
    //  post.getCategoryId() == 0 ? null : post.getCategoryId()
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
                "WHERE post.id = ?";

        List<PostDto> results = jdbcTemplate.query(sql, postDtoRowMapper, postId);
        return results.isEmpty() ? null : results.get(0);
    }
    // 레시피 게시글 개수
    @Override
    public int recipePostCount() {
        String sql = "SELECT COUNT(*) FROM post WHERE post_type = 1";
        // queryForObject의 반환형이 Integer라 이렇게 받음
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        // Integer는 0이 null이여서 이렇게 조건 처리
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
    public List<PostDto> findRecipePosts(int categoryId, String type, int offset, int limit) {
        String sql = "SELECT post.* , category.category_name, member.name FROM post " +
                "LEFT JOIN category on post.category_id = category.id " +
                "LEFT JOIN member on post.member_id = member.id " +
                "WHERE post.post_type = 1 ";

        // 카테고리
        if(categoryId != 0) {
            sql += " AND post.category_id = ?";
        }
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

        // 카테고리가 있으면 반환을 카테고리도 해줘야함
        if(categoryId != 0) {
            return jdbcTemplate.query(sql, postDtoRowMapper, categoryId, offset, limit);
        } else {
            return jdbcTemplate.query(sql, postDtoRowMapper, offset, limit);
        }
    }


    // 꿀팁 게시글 페이징 (post_type = 2)
    // 페이지당 8개씩 보여줄 것임
    // 0, 8 넣고 Service에서 계산
    @Override
    public List<PostDto> findTipPosts(int offset, int limit) {
        String sql = "SELECT p.*, m.name AS member_name " +
                     "FROM post p " +
                     "LEFT JOIN member m ON p.member_id = m.id " +
                     "WHERE p.post_type = 2 " +
                     "ORDER BY p.created_at DESC " +
                     "LIMIT ?, ?";
        return jdbcTemplate.query(sql, postDtoRowMapper, offset, limit);
    }

    // 꿀팁 게시글 개수
    @Override
    public int tipPostCount() {
        String sql = "SELECT COUNT(*) FROM post WHERE post_type = 2";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;
    }

    @Override
    public List<PostDto> search(String type, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String searchKeyword = "%" + keyword.trim() + "%";
        if ("title".equals(type)) {
            return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE p.title LIKE ? ORDER BY p.id DESC", postDtoRowMapper, searchKeyword);
        } else if ("content".equals(type)) {
            return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE p.content LIKE ? ORDER BY p.id DESC", postDtoRowMapper, searchKeyword);
        } else if ("author".equals(type)) {
            return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE m.name LIKE ? ORDER BY p.id DESC", postDtoRowMapper, searchKeyword);
        } else {
            return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE p.title LIKE ? OR p.content LIKE ? ORDER BY p.id DESC", postDtoRowMapper, searchKeyword, searchKeyword);
        }
    }

    @Override
    public int viewCount(int postId) {
        String sql = "UPDATE post SET view_count = view_count + 1 WHERE id = ? ";
        return jdbcTemplate.update(sql, postId);
    }

    /**
     * {@inheritDoc}
     * LIMIT ? OFFSET ? 구문을 활용해 원하는 개수만큼 잘라서 가져오는 데이터베이스 페이징 조회를 수행합니다.
     */
    @Override
    public List<PostDto> search(String type, String keyword, int offset, int limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return jdbcTemplate.query(BASE_SELECT_SQL + " ORDER BY p.id DESC LIMIT ? OFFSET ?", postDtoRowMapper, limit, offset);
        }
        String searchKeyword = "%" + keyword.trim() + "%";
        if ("title".equals(type)) {
            return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE p.title LIKE ? ORDER BY p.id DESC LIMIT ? OFFSET ?", postDtoRowMapper, searchKeyword, limit, offset);
        } else if ("content".equals(type)) {
            return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE p.content LIKE ? ORDER BY p.id DESC LIMIT ? OFFSET ?", postDtoRowMapper, searchKeyword, limit, offset);
        } else if ("author".equals(type)) {
            return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE m.name LIKE ? ORDER BY p.id DESC LIMIT ? OFFSET ?", postDtoRowMapper, searchKeyword, limit, offset);
        } else {
            return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE p.title LIKE ? OR p.content LIKE ? ORDER BY p.id DESC LIMIT ? OFFSET ?", postDtoRowMapper, searchKeyword, searchKeyword, limit, offset);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count(String type, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post", Integer.class);
            return count != null ? count : 0;
        }
        String searchKeyword = "%" + keyword.trim() + "%";
        if ("title".equals(type)) {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post p WHERE p.title LIKE ?", Integer.class, searchKeyword);
            return count != null ? count : 0;
        } else if ("content".equals(type)) {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post p WHERE p.content LIKE ?", Integer.class, searchKeyword);
            return count != null ? count : 0;
        } else if ("author".equals(type)) {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post p JOIN member m ON p.member_id = m.id WHERE m.name LIKE ?", Integer.class, searchKeyword);
            return count != null ? count : 0;
        } else {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post p WHERE p.title LIKE ? OR p.content LIKE ?", Integer.class, searchKeyword, searchKeyword);
            return count != null ? count : 0;
        }
    }

}

