package net.likelion.bebc25.recipe.post.service;

import net.likelion.bebc25.recipe.post.dto.PostDto;

import java.util.List;

public interface PostService {
    // 게시글 전체 조회
    List<PostDto> getPosts();
    // 레시피 게시글
    List<PostDto> getRecipePosts();
    //요리 꿀팁 게시글
    List<PostDto> getTipPosts(int type);
    // 게시글 조회
    PostDto getPost(int id);
    // 게시글 작성
    void writePost(PostDto postDto);
    // 게시글 수정
    void editPost(PostDto postDto);
    // 게시글 삭제
    void removePost(int id);
    // 아이디를 매개변수로 글 조회
    List<PostDto> getPosts(int memberId);
    // 아이디를 매개변수로 레시피 글 조회
    List<PostDto> getRecipePost(int memberId);
    // 아이디를 매개변수로 요리꿀팁 글 조회[[새로추가됨]]
    List<PostDto> getTipPost(int memberId);
    // postId를 매개변수로 레시피 1건 상세조회
    PostDto getRecipe(int postId);
    // 레시피 게시글 총 개수
    int recipePostCount();
    // 오늘 뭐먹지 게시글
    PostDto getRandomRecipePost(int categoryId);
    // 레시피 게시글 페이징, 조회순, 최신순 , 카테고리
    List<PostDto> getRecipePosts(int categoryId, String type, int page, int size);

    List<PostDto> searchPosts(String type, String keyword, int page, int size);
    int searchPostCount(String type, String keyword);
    // 꿀팁 게시글 페이징
    List<PostDto> getTipPosts(int page, int size);
    // 꿀팁 게시글 개수
    int tipPostCount();

}
