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

    @Override
    public void saveFollowMember(int followingId, int followerId) {
        followRepository.save(followingId, followerId);
    }

    @Override
    public List<FollowDto> myFollowing(int followingId) {
        return followRepository.findFollowingById(followingId);
    }

    @Override
    public List<FollowDto> myFollowers(int followerId) {
        return List.of();
    }

    @Override
    public FollowDto findFollowById(int followingId, int followerId) {
        return followRepository.findFollowById(followingId, followerId);
    }

    @Override
    public void deleteFollowMember(int followingId, int followerId) {
        followRepository.delete(followingId, followerId);
    }

    @Override
    public List<MemberDto> myFollowingMembers(int followingId) {
        List<FollowDto> myFollowingID = followRepository.findFollowingById(followingId);

        List<MemberDto> myFollowingMembers = new ArrayList<>();
        for (FollowDto following : myFollowingID) {
            myFollowingMembers.add(memberRepository.findById(following.getFollowerId()));
        }

        return myFollowingMembers;
    }

    @Override
    public List<PostDto> myFollowingMembersRecipes(int followingId) {
        List<MemberDto> myFollowingMembers = myFollowingMembers(followingId);

        List<PostDto> myFollowingMembersRecipes = new ArrayList<>();
        for (MemberDto member : myFollowingMembers) {
            List<PostDto> posts = postRepository.findPostByUser(member.getId());

            if (posts != null && !posts.isEmpty()) {
                myFollowingMembersRecipes.addAll(posts);
            }
        }

        myFollowingMembersRecipes.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));

        return  myFollowingMembersRecipes;
    }
}
