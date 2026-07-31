package net.likelion.bebc25.recipe.follow.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class FollowDto {
    private int id;
    private int followingId;
    private int followerId;
}
