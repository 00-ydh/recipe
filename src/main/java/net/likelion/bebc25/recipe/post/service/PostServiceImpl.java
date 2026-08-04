package net.likelion.bebc25.recipe.post.service;

import net.likelion.bebc25.recipe.post.dto.PostDto;
import net.likelion.bebc25.recipe.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(@Qualifier("jdbcPostRepository") PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<PostDto> getPosts() {
        return List.of();
    }

    // 레시피 게시글
    @Override
    public List<PostDto> getRecipePosts() {
        return postRepository.findRecipePosts();
    }
    @Override
    public List<PostDto> getTipPosts(int type) {
        return postRepository.findTipPosts();
    }

    @Override
    public PostDto getPost(int id) {
        return postRepository.findById(id);
    }

    @Override
    public void writePost(PostDto postDto) {
        postRepository.save(postDto);
    }

    @Override
    public void editPost(PostDto postDto) {
        postRepository.update(postDto);
    }
    @Override
    public void removePost(int id) {
        postRepository.deleteById(id);
    }

    @Override
    public List<PostDto> getPosts(int memberId) {
        return postRepository.findPostByUser(memberId);
    }

    @Override
    public List<PostDto> getRecipePost(int memberId) {
        return postRepository.findRecipePostByUser(memberId);
    }

//    요리꿀팁 게시글 추가함!
    @Override
    public List<PostDto> getTipPost(int memberId) {
        return postRepository.findTipPostByUser(memberId);
    }
    // 레시피 postId로 상세 조회
    @Override
    public PostDto getRecipe(int postId) {
        return postRepository.findRecipePostByPostId(postId);
    }

    // 레시피 게시글 페이징
    @Override
    public int recipePostCount() {
        return postRepository.recipePostCount();
    }

    // 오늘 뭐먹지
    @Override
    public PostDto getRandomRecipePost(int categoryId) {
        return postRepository.findTodayPostByCategoryId(categoryId);
    }
    // offset 0 limit 8 - 페이지당 8개씩 보여주기
    // type - latest(최신)/ view(조회순)
    public List<PostDto> getRecipePosts(int categoryId, String type, int page, int size){
        int validPage = page < 1 ? 1 : page;
        int validSize = size < 1 ? 8 : size;
        int offset = (validPage - 1) * validSize;

        return postRepository.findRecipePosts(categoryId, type, offset, validSize);

    }


    //검색

    @Override
    public int searchPostCount(String type, String keyword) {
        return postRepository.count(type, keyword);
    }
    @Override
    public List<PostDto> searchPosts(String type, String keyword, int page, int size) {
        int validPage = page < 1 ? 1 : page;
        int validSize = size < 1 ? 10 : size;
        int offset = (validPage - 1) * validSize;
        return postRepository.search(type, keyword, offset, validSize);
    }

}
