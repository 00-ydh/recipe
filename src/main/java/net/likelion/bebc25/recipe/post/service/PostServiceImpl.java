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

    }
    @Override
    public void removePost(int id) {

    }
}
