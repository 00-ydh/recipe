package net.likelion.bebc25.recipe.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class MemberDto {
    /**
     * 회원 고유 식별자
     */
    private int id;

    /**
     * 회원 별명
     */
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하여야 합니다.")
    private String name;

    /**
     * 회원 이메일 주소
     */
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    /**
     * 회원 비밀번호
     */
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 4, max = 20, message = "비밀번호는 4자 이상 20자 이하여야 합니다.")
    private String password;

    /**
     * 회원 가입 일시
     */
    private LocalDateTime createdAt;
}
