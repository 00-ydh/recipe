package net.likelion.bebc25.recipe.member.repository;

import net.likelion.bebc25.recipe.member.dto.MemberDto;

import java.util.List;

public interface MemberRepository {
    /**
     * 새로운 회원 정보를 저장합니다.
     * @param memberDto 저장할 회원 정보 DTO
     */
    void save(MemberDto memberDto);

    /**
     * 회원 고유 식별자를 기반으로 회원 정보를 조회합니다.
     * @param id 조회할 회원 고유 식별자
     * @return 조회된 회원 정보 DTO, 없을 경우 null 반환
     */
    MemberDto findById(int id);

    /**
     * 회원의 이름을 수정합니다.
     * @param memberId 수정할 회원 ID
     * @param newName 수정할 이름
     */
    void updateName(int memberId, String newName);

    /**
     * 회원의 비밀번호를 수정합니다.
     * @param memberId 수정할 회원 ID
     * @param newPassword 수정할 비밀번호
     */
    void updatePassword(int memberId, String newPassword);

    /**
     * 회원 고유 식별자를 기반으로 회원 정보를 삭제합니다.
     * @param id 삭제할 회원 고유 식별자
     */
    void deleteById(int id);

    /**
     * 저장소에 있는 모든 회원 정보를 조회합니다.
     * @return 전체 회원 목록
     */
    List<MemberDto> findAll();

    /**
     * 회원 이메일을 기반으로 회원 정보를 조회합니다.
     * @param email 조회할 로그인 이메일
     * @return 조회된 회원 정보 DTO, 없을 경우 null 반환
     */
    MemberDto findByEmail(String email);

    /**
     * 회원 이름을 기반으로 회원 정보를 조회합니다.
     * @param name 조회할 회원의 이름
     * @return 조회된 회원 정보 DTO
     */
    MemberDto findByName(String name);

    /**
     * 데이터베이스에 같은 이메일이 있는 지 확인합니다.
     * @param email 확인할 이메일
     * @return 같은 이메일이 존재하면 true, 없으면 false 반환
     */
    boolean existsByEmail(String email);

    /**
     * 데이터베이스에 같은 이름이 있는 지 확인합니다.
     * @param name 확인할 이름
     * @return 같은 이름 존재하면 true, 없으면 false 반환
     */
    boolean existsByName(String name);
}
