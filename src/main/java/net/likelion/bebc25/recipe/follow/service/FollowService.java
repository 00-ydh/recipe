package net.likelion.bebc25.recipe.follow.service;

import net.likelion.bebc25.recipe.follow.dto.FollowDto;
import net.likelion.bebc25.recipe.member.dto.MemberDto;
import net.likelion.bebc25.recipe.post.dto.PostDto;

import java.util.List;

public interface FollowService {

    /**
     * 대상 회원을 팔로우합니다.
     * @param followingId 팔로우를 신청하는 회원 ID
     * @param followerId 팔로우 대상 회원 ID
     */
    void saveFollowMember(int followingId, int followerId);

    List<FollowDto> myFollowing(int followingId);

    List<FollowDto> myFollowers(int followerId);

    /**
     * 두 회원 간의 팔로우 관계 여부를 조회합니다.
     * @param followingId 팔로우를 한 회원 ID
     * @param followerId  팔로우를 받은 회원 ID
     * @return 팔로우 상태이면 FollowDto, 아니면 null
     */
    FollowDto findFollowById(int followingId, int followerId);

    /**
     * 대상 회원을 언팔로우(팔로우 취소)합니다.
     * @param followingId 팔로우를 취소하는 회원 ID (나)
     * @param followerId  언팔로우 대상 회원 ID (상대방)
     */
    void deleteFollowMember(int followingId, int followerId);

    /**
     * 내가 팔로우한 회원들의 회원 정보 목록을 조회합니다.
     * @param followingId 로그인한 회원 ID (나)
     * @return 팔로우한 회원들의 정보(MemberDto) 리스트
     */
    List<MemberDto> myFollowingMembers(int followingId);

    /**
     * 내가 팔로우한 회원들이 작성한 레시피 게시글 목록을 조회합니다.
     * @param followingId 로그인한 회원 ID (나)
     * @return 팔로우한 회원들의 레시피 게시글(PostDto) 리스트
     */
    List<PostDto> myFollowingMembersRecipes(int followingId);

    int getFollowerCount(int memberId);
}
