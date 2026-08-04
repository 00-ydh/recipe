package net.likelion.bebc25.recipe.follow.service;

import net.likelion.bebc25.recipe.follow.dto.FollowDto;
import net.likelion.bebc25.recipe.follow.repository.FollowRepository;
import net.likelion.bebc25.recipe.member.dto.MemberDto;
import net.likelion.bebc25.recipe.member.repository.MemberRepository;
import net.likelion.bebc25.recipe.post.dto.PostDto;
import net.likelion.bebc25.recipe.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public FollowServiceImpl(FollowRepository followRepository,  MemberRepository memberRepository, PostRepository postRepository) {
        this.followRepository = followRepository;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveFollowMember(int followingId, int followerId) {
        followRepository.save(followingId, followerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FollowDto> myFollowing(int followingId) {
        return followRepository.findFollowingById(followingId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FollowDto> myFollowers(int followerId) {
        return List.of();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FollowDto findFollowById(int followingId, int followerId) {
        return followRepository.findFollowById(followingId, followerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFollowMember(int followingId, int followerId) {
        followRepository.delete(followingId, followerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MemberDto> myFollowingMembers(int followingId) {
        List<FollowDto> myFollowingID = followRepository.findFollowingById(followingId);

        List<MemberDto> myFollowingMembers = new ArrayList<>();
        for (FollowDto following : myFollowingID) {
            myFollowingMembers.add(memberRepository.findById(following.getFollowerId()));
        }

        return myFollowingMembers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PostDto> myFollowingMembersRecipes(int followingId) {
        return postRepository.findFollowingMemberRecipes(followingId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFollowerCount(int memberId) {
        return followRepository.countFollowers(memberId);
    }
}
