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

}
