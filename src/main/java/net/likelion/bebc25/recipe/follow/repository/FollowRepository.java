package net.likelion.bebc25.recipe.follow.repository;

import net.likelion.bebc25.recipe.follow.dto.FollowDto;

import java.util.List;

public interface FollowRepository {

    void save(int followingId, int followerId);

    void delete(int followingId, int followerId);

    List<FollowDto> findFollowingById(int followingId);

    List<FollowDto> findFollowerById(int followerId);

    FollowDto findFollowById(int followingId, int followerId);

}
