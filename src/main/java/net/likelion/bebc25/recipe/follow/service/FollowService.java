package net.likelion.bebc25.recipe.follow.service;

import net.likelion.bebc25.recipe.follow.dto.FollowDto;
import net.likelion.bebc25.recipe.member.dto.MemberDto;
import net.likelion.bebc25.recipe.post.dto.PostDto;

import java.util.List;

public interface FollowService {

    void saveFollowMember(int followingId, int followerId);

    List<FollowDto> myFollowing(int followingId);

    List<FollowDto> myFollowers(int followerId);

    FollowDto findFollowById(int followingId, int followerId);

    void deleteFollowMember(int followingId, int followerId);

    List<MemberDto> myFollowingMembers(int followingId);

    List<PostDto> myFollowingMembersRecipes(int followingId);
}
