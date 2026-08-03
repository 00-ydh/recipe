package net.likelion.bebc25.recipe.member.repository;

import net.likelion.bebc25.recipe.member.dto.MemberDto;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcTemplateMemberRepository implements MemberRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * 생성자를 통해 의존하는 JdbcTemplate을 주입받습니다.
     * @param jdbcTemplate 스프링 빈으로 등록된 JdbcTemplate 객체
     */
    public JdbcTemplateMemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * 데이터베이스 ResultSet 데이터를 MemberDto 객체로 변환하는 맵퍼 정의입니다.
     */
    private final RowMapper<MemberDto> memberDtoRowMapper = (ResultSet rs, int rowNum) -> {
        return MemberDto.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .name(rs.getString("name"))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .build();
    };


    /**
     * 새로운 회원 정보(이메일, 비밀번호, 별명)를 입력 받아 저장합니다.
     * 회원 고유 식별자, 회원 생성 일시는 자동으로 추가됩니다.
     * @param memberDto 저장할 회원 정보 DTO
     */
    @Override
    public void save(MemberDto memberDto) {
        jdbcTemplate.update("INSERT INTO member (email, password, name) VALUES (?, ?, ?)",
                memberDto.getEmail(),
                memberDto.getPassword(),
                memberDto.getName());
    }

    /**
     * 회원 고유 식별자를 기반으로 회원 정보를 조회합니다.
     * @param id 조회할 회원 고유 식별자
     * @return 조회된 회원 정보
     */
    @Override
    public MemberDto findById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM member WHERE id = ?", memberDtoRowMapper, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateName(int memberId, String newName) {
        jdbcTemplate.update("UPDATE member SET name = ? WHERE id = ?", newName, memberId);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updatePassword(int memberId, String newPassword) {
        jdbcTemplate.update("UPDATE member SET password = ? WHERE id = ?", newPassword, memberId);

    }

    /**
     * 회원 고유 식별자에 해당하는 회원을 삭제합니다.
     * @param id 삭제할 회원 고유 식별자
     */
    @Override
    public void deleteById(int id) {
        jdbcTemplate.update("DELETE FROM member WHERE id = ?", id);
    }

    /**
     * 모든 회원 정보를 조회합니다.
     * @return 전체 회원 목록
     */
    @Override
    public List<MemberDto> findAll() {
        return jdbcTemplate.query("SELECT * FROM member", memberDtoRowMapper);
    }

    /**
     * 회원 이메일을 기반으로 회원 정보를 조회합니다.
     * @param email 조회할 로그인 이메일
     * @return 조회된 회원 정보 DTO, 없을 경우 null 반환
     */
    @Override
    public MemberDto findByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM member WHERE email = ?", memberDtoRowMapper, email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MemberDto findByName(String name) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM member WHERE name = ?", memberDtoRowMapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM member WHERE eamil = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM member WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }


}
