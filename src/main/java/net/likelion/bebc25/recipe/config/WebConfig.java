package net.likelion.bebc25.recipe.config;

import net.likelion.bebc25.recipe.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/member/**", "/tip/write", "/tip/edit", "/tip/delete", "/follow")
                .excludePathPatterns("/member/login", "/member/register", "/member/profile/**", "/member/css/**", "/js/**", "/images/**", "/*.ico", "/error");
    }
}
