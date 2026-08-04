package net.likelion.bebc25.recipe.follow.repository;

import net.likelion.bebc25.recipe.follow.dto.FollowDto;
import net.likelion.bebc25.recipe.post.dto.PostDto;

import java.util.List;

public interface FollowRepository {

    /**
     * 팔로우 관계를 저장합니다.
     * @param followingId 팔로우를 신청하는 회원 ID
     * @param followerId 팔로우 대상 회원 ID
     */
    void save(int followingId, int followerId);

    /**
     * 팔로우 관계를 삭제합니다.
     * @param followingId 팔로우를 신청하는 회원 ID
     * @param followerId 팔로우 대상 회원 ID
     */
    void delete(int followingId, int followerId);

    /**
     * 특정 회원이 팔로우하고 있는 목록을 조회합니다.
     * @param followingId 기준 회원 ID
     * @return 해당 회원이 팔로우 중인 FollowDto 목록
     */
    List<FollowDto> findFollowingById(int followingId);

    List<FollowDto> findFollowerById(int followerId);

    /**
     * 두 회원 간의 팔로우 관계 존재 여부를 조회합니다.
     * @param followingId 팔로우를 신청하는 회원 ID
     * @param followerId 팔로우 대상 회원 ID
     * @return 조회된 FollowDTO
     */
    FollowDto findFollowById(int followingId, int followerId);

    /**
     * 특정 회원의 팔로워 수를 조회합니다.
     * @param memberId 조회하고 싶은 회원 ID
     * @return memberId의 팔로워 수
     */
    int countFollowers(int memberId);
}
