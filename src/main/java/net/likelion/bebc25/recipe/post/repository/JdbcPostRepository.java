package net.likelion.bebc25.recipe.post.repository;

import net.likelion.bebc25.recipe.post.dto.PostDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcPostRepository implements  PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public List<PostDto> findAll() {
        return List.of();
    }

    @Override
    public List<PostDto> findRecipePosts(int type) {
        return List.of();
    }

    @Override
    public List<PostDto> findTipPosts(int type) {
        return List.of();
    }

    @Override
    public PostDto findById(int id) {
        return null;
    }

    @Override
    public void save(PostDto post) {

    }

    @Override
    public void update(PostDto post) {

    }

    @Override
    public void deleteById(int id) {

    }
}
