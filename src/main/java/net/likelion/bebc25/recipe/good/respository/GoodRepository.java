package net.likelion.bebc25.recipe.good.respository;

import net.likelion.bebc25.recipe.good.dto.GoodDto;

public interface GoodRepository {


        void save(GoodDto goodDTO);
        void delete(GoodDto goodDTO);
        boolean exists(GoodDto goodDTO);

}