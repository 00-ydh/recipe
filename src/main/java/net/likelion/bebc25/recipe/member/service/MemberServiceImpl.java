package net.likelion.bebc25.recipe.member.service;

import net.likelion.bebc25.recipe.exception.DuplicateEmailException;
import net.likelion.bebc25.recipe.exception.DuplicateNameException;
import net.likelion.bebc25.recipe.member.dto.MemberDto;
import net.likelion.bebc25.recipe.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     */
    @Override
    @Transactional
    public void register(MemberDto memberDto) {
        if (memberRepository.existsByEmail(memberDto.getEmail())) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        if (memberRepository.existsByName(memberDto.getName())) {
            throw new DuplicateNameException("이미 사용 중인 이름입니다.");
        }

        memberRepository.save(memberDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MemberDto login(String email, String password) {
        MemberDto memberDto = memberRepository.findByEmail(email);

        if (memberDto == null || !(memberDto.getPassword().equals(password))){
            return null;
        } else {
            return memberDto;
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void editName(int memberId, String newName) {
        if (memberRepository.existsByName(newName)) {
            throw new DuplicateNameException("이미 사용 중인 이름입니다.");
        }

        memberRepository.updateName(memberId, newName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void editPassword(int memberId, String newPassword) {
        memberRepository.updatePassword(memberId, newPassword);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MemberDto getMemberById(int id) {
        return memberRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteMemberById(int id) {
        memberRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MemberDto getMemberByName(String name) {
        return memberRepository.findByName(name);
    }
}
