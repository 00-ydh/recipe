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
    // 레시피 게시글 조회순 / 최신글 순 정렬
    List<PostDto> findRecipePosts(String type, int offset, int limit);

}
