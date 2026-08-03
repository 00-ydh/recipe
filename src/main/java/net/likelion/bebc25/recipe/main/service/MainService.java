package net.likelion.bebc25.recipe.main.service;

import net.likelion.bebc25.recipe.main.dto.MainDto;

import java.util.List;

public interface MainService {

    // 명예의 전당: 좋아요 많은 레시피 TOP 3
    List<MainDto> getTopRecipes();

    // 밥플루언서: 팔로워 많은 멤버 TOP 5
    List<MainDto> getTopMember();

    // 이달의 추천: 한 달 이내 좋아요 많은 레시피 TOP 10
    List<MainDto> getMonthlyTopRecipes();

    // 최신 요리 꿀팁: 최신순 6개
    List<MainDto> getLatestTipPosts();

}