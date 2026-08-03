package net.likelion.bebc25.recipe.good.respository;

import net.likelion.bebc25.recipe.good.dto.GoodDto;
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
public class GoodRepositoryImpl implements GoodRepository{
    private final JdbcTemplate jdbcTemplate;

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

    public GoodRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void save(GoodDto goodDto) {
        String sql = "INSERT INTO good (post_id, member_id, like_type) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, goodDto.getPostId(), goodDto.getMemberId(), goodDto.getLikeType());
    }

    @Override
    public void delete(GoodDto goodDTO) {
        String sql = "DELETE FROM good WHERE post_id = ? AND member_id = ? AND like_type = ?";
        jdbcTemplate.update(sql, goodDTO.getPostId(), goodDTO.getMemberId(), goodDTO.getLikeType());
    }

    @Override
    public boolean exists(GoodDto goodDto) {
        //지금 사용자가 보고 있는 게시글 번호, 현재 로그인한 회원의 번호, 어떤 게시판인지 구분하는 게시판 타입코드
        //3개가 모두 일치하는 데이터를 찾고
        //좋아요를 누르지 않은경우 count결과가 0일때 사용자가 좋아요를 처음 누른 것이니까 db에 새로운 좋아요 데이터를 insert
        //해준다
        //결과가 1이상일 때 사용자가 좋아요를 다시 눌러서 취소하려는 것으로 db에 있는 좋아요 데이터를 삭제 한다.
        //좋아요 로직은 잘 따라와보자.
        String sql = "SELECT COUNT(*) FROM good WHERE post_id = ? AND member_id = ? AND like_type = ?";
        Integer count = jdbcTemplate.queryForObject(
                sql, Integer.class,
                goodDto.getPostId(), goodDto.getMemberId(), goodDto.getLikeType()
        );
        return count != null && count > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PostDto> getScrapRecipes(int memberId) {
        String sql = "SELECT p.*, m.name, c.category_name FROM good g LEFT JOIN post p on g.post_id = p.id " +
                "LEFT JOIN member m on p.member_id = m.id " +
                "LEFT JOIN category c on p.category_id = c.id " +
                "WHERE g.like_type = 3 AND p.post_type = 1 AND g.member_id = ?";
        return jdbcTemplate.query(sql, postDtoRowMapper, memberId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PostDto> getScrapTips(int memberId) {
        String sql = "SELECT p.*, m.name FROM good g LEFT JOIN post p on g.post_id = p.id " +
                "LEFT JOIN member m on p.member_id = m.id " +
                "WHERE g.like_type = 3 AND p.post_type = 2 AND g.member_id = ?";
        return jdbcTemplate.query(sql, postDtoRowMapper, memberId);
    }
}
