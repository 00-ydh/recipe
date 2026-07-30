package net.likelion.bebc25.recipe.reply.repository;

import net.likelion.bebc25.recipe.reply.dto.RequestDTO;
import net.likelion.bebc25.recipe.reply.dto.ResponseDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JDBCTemplateRepository implements ReplyRepository{

    private final JdbcTemplate  jdbcTemplate;

    public JDBCTemplateRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    //
    private final RowMapper<ResponseDTO> replyRowMapper = (ResultSet rs, int rowNum) -> {
        return ResponseDTO.builder()
                //responsedto에 실제 데이터베이스(컬럼명)이 주는 표를 가지고 rowmapper가 넣어준다.
                .id(rs.getInt("id"))
                .postId(rs.getLong("post_id"))
                .memberId(rs.getLong("member_id"))
                .content(rs.getString("content"))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .name(rs.getString("name")) //member테이블에서 join할 예정
                .build();
    };

    @Override
    public void save(RequestDTO requestDTO) {
        //insert 문을통한 post_id, member_id, content 삽입!
        String sql = "INSERT INTO reply (post_id, member_id, content) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql,
                requestDTO.getPostId(),
                requestDTO.getMemberId(),
                requestDTO.getContent());

    }

    @Override
    public List<ResponseDTO> findByPostId(Long postId) {
        //댓글 테이블에 있는 모든 기본 정보를 가져오고
        String sql = "SELECT r.*, m.name FROM reply r " +
                //댓글을 쓴 회원의번호와 회원 테이블의 고유번호가 일치하는 사람을
                //찾아 작성자 테이블 옆에 갖다 붙인다(join)
                "LEFT JOIN member m ON r.member_id = m.id " +
                //매개변수 Long post id가 여기 들어온다.
                "WHERE r.post_id = ? " +
                "ORDER BY r.id DESC"; // 최신 댓글이 위로 오게 정렬
        //jdbctemplate를 이용해 쿼리를 실행, 결과를 list<response dto>로 변환해 반환
        return jdbcTemplate.query(sql, replyRowMapper, postId);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM reply WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
