package net.likelion.bebc25.recipe.post.repository;

import net.likelion.bebc25.recipe.post.dto.PostDto;

import java.util.List;

public interface PostRepository {
    // 게시글 전체 조회
    List<PostDto> findAll();
    List<PostDto> findRecipePosts();
    List<PostDto> findTipPosts();
    // 게시글 한건 조회
    PostDto findById(int id);
    // 게시글 등록
    void save(PostDto post);
    // 게시글 수정
    void update(PostDto post);
    // 게시글 삭제
    void deleteById(int id);
    // 아이디로 게시글 조회
    List<PostDto> findPostByUser(int memberId);
    // 아이디로 레시피 게시글 조회
    List<PostDto> findRecipePostByUser(int memberId);
    // 아이디로 꿀팁 게시글 조회[[새로 추가됨]]
    List<PostDto> findTipPostByUser(int memberId);
    // postId로 Recipe 게시글 상세 조회
    PostDto findRecipePostByPostId(int postId);
    // 레시피 게시글 개수
    int recipePostCount();
    // 오늘뭐먹지 게시글 카테고리 아이디로 하나 띄우기
    PostDto findTodayPostByCategoryId(int categoryId);
    // 레시피 게시글 조회순 / 최신글 순 정렬 /카테고리별 레시피 게시글 페이징
    List<PostDto> findRecipePosts(int categoryId, String type, int offset, int limit);
    //검색 조건에 맞는 전체 게시글 개수 조회
    int count(String type, String keyword);
    //검색 조건 및 페이징이 적용된 게시글 목록 조회
    List<PostDto> search(String type, String keyword, int offset, int limit);
    List<PostDto> search(String type, String keyword);
    // 꿀팁 게시글 페이징
    List<PostDto> findTipPosts(int offset, int limit);
    // 꿀팁 게시글 개수
    int tipPostCount();
    // 조회수
    int viewCount(int postId);
    void increaseLikeCount(int postId);
    void decreaseLikeCount(int postId);

    /**
     * 내가 팔로잉한 회원들의 레시피 조회 (내림차순, 최신순)
     * @param followingId 팔로잉한 회원 ID (나)
     * @return 조회된 PostDto 리스트
     */
    List<PostDto> findFollowingMemberRecipes(int followingId);

    /**
     * 입력 받은 회원 ID로 해당 회원이 스크랩한 레시피 조회
     * @param memberId 조회할 회원 ID
     * @return 조회된 레시피 리스트
     */
    List<PostDto> getScrapRecipes(int memberId);

    /**
     * 입력 받은 회원 ID로 해당 회원이 스크랩한 레시피 조회
     * @param memberId 조회할 회원 ID
     * @return 조회된 레시피 리스트
     */
    List<PostDto> getScrapTips(int memberId);
}
