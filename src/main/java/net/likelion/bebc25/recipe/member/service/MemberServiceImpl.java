package net.likelion.bebc25.recipe.member.service;

import net.likelion.bebc25.recipe.member.dto.MemberDto;
import net.likelion.bebc25.recipe.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    /**
     * 생성자를 통해 MemberRepository 의존성을 주입 받습니다.
     * @param memberRepository 주입 받을 MemberRepository 스프링 빈 객체
     */
    public MemberServiceImpl(@Qualifier("jdbcTemplateMemberRepository") MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원 가입을 처리합니다. memberDto의 email이 데이터베이스에 존재하지 않을 경우에 회원 가입을 허용합니다.
     * @param memberDto 회원 가입을 요청한 회원 정보 DTO
     * @return 성공할 경우 true, 실패할 경우 false
     */
    @Override
    public boolean register(MemberDto memberDto) {
        if(memberRepository.findByEmail(memberDto.getEmail()) == null){
            memberRepository.save(memberDto);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public MemberDto login(String email, String password) {
        MemberDto memberDto = memberRepository.findByEmail(email);

        if (memberDto == null){
            return null;
        }

        if (memberDto.getPassword().equals(password)){
            return memberDto;
        } else {
            return null;
        }
    }

    @Override
    public String findPassword(String email, String name) {
        MemberDto memberDto = memberRepository.findByEmail(email);

        if (memberDto == null){
            return "존재하지 않는 이메일입니다.";
        }

        if (!(memberDto.getName().equals(name))){
            System.out.println(memberDto.getName());
            System.out.println(name);
            return "별명이 다릅니다.";
        } else {
            return memberDto.getPassword() + " 입니다.";
        }
    }


}
