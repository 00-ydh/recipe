package net.likelion.bebc25.recipe.follow.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class FollowDto {
    /**
     * 팔로우 고유 식별자
     */
    private int id;

    /**
     * 팔로잉을 요구하는 회원 ID (나)
     */
    private int followingId;

    /**
     * 내가 팔로잉 한 회원 ID (대상)
     */
    private int followerId;
}
