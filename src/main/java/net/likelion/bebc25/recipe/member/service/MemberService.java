package net.likelion.bebc25.recipe.member.service;

import net.likelion.bebc25.recipe.member.dto.MemberDto;

public interface MemberService {
    /**
     * 회원 가입을 처리합니다.
     * @param memberDto 회원 가입을 요청한 회원 정보 DTO
     */
    void register(MemberDto memberDto);

    /**
     * 로그인을 처리하기 위해 아이디(이메일)과 비밀번호로 회원을 인증합니다.
     * @param email 로그인 이메일
     * @param password 로그인 비밀번호
     * @return 인증에 성공한 회원 정보 DTO, 실패 시 null 반환
     */
    MemberDto login(String email, String password);

    /**
     * 이메일과 별명을 입력받아 비밀번호를 조회합니다.
     * @param email 회원 이메일
     * @param name 회원 별명
     */
    String findPassword(String email, String name);


    void editName(int memberId, String newName);

    void editPassword(int memberId, String newPassword);

    /**
     * 회원 고유 식별자를 기반으로 회원 정보 DTO를 검색하여 가져옵니다.
     * @param id 회원 고유 식별자
     * @return 회원 정보 DTO
     */
    MemberDto getMemberById(int id);

    /**
     * 회원 고유 식별자를 기반으로 회원 정보를 삭제합니다.
     * @param id 회원 고유 식별자
     */
    void deleteMemberById(int id);

    /**
     * 회원 별명을 기반으로 회원 정보 DTO를 검색하여 가져옵니다.
     * @param name
     * @return
     */
    MemberDto getMemberByName(String name);
}
