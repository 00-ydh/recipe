package net.likelion.bebc25.recipe.main.service;

import net.likelion.bebc25.recipe.main.dto.MainDto;
import net.likelion.bebc25.recipe.main.repository.MainRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class MainServiceImpl implements MainService{

    private final MainRepository mainRepository;


    public MainServiceImpl(@Qualifier("jdbcMainRepository") MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    //  명예의 전당 (기간x)
    @Override
    public List<MainDto> getTopRecipes() {
        return mainRepository.findTopRecipes();
    }

    //  밥풀루언서
    @Override
    public List<MainDto> gettopFollowMembers() {
        return mainRepository.findTopMember();
    }

    // 이달의 추천 레시피 (기간 : 한날이내)
    @Override
    public List<MainDto> getMonthlyTopRecipes() {
        return mainRepository.findMonthlyTopRecipes();
    }

    // 최신 요리 꿀팁 (최신순 6개)
    @Override
    public List<MainDto> getLatestTipPosts() {
        return mainRepository.findLatestTipPosts();
    }
}
