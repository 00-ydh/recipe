package net.likelion.bebc25.recipe.good.respository;

import net.likelion.bebc25.recipe.good.dto.GoodDto;
import net.likelion.bebc25.recipe.post.dto.PostDto;

import java.util.List;

public interface GoodRepository {


        void save(GoodDto goodDTO);
        void delete(GoodDto goodDTO);
        boolean exists(GoodDto goodDTO);

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