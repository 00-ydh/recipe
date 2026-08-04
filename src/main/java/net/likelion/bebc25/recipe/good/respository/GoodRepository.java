package net.likelion.bebc25.recipe.good.respository;

import net.likelion.bebc25.recipe.good.dto.GoodDto;
import net.likelion.bebc25.recipe.post.dto.PostDto;

import java.util.List;

public interface GoodRepository {


        void save(GoodDto goodDTO);
        void delete(GoodDto goodDTO);
        boolean exists(GoodDto goodDTO);

}