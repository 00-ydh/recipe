package net.likelion.bebc25.recipe.good.respository;

import net.likelion.bebc25.recipe.good.dto.GoodDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GoodRepositoryImpl implements GoodRepository{
    private final JdbcTemplate jdbcTemplate;

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








}
