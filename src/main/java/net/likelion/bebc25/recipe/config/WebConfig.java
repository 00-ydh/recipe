package net.likelion.bebc25.recipe.config;

import net.likelion.bebc25.recipe.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.mainImageDir}")
    private String fileDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/member/**", "/tip/write", "/tip/edit", "/tip/delete", "/recipe/write", "/recipe/edit", "/recipe/delete", "/follow")
                .excludePathPatterns("/member/login", "/member/register", "/member/profile/**", "/member/css/**", "/js/**", "/images/**", "/*.ico", "/error");
    }
    // 브라우저의 /images/** 요청 경로를 외부 로컬 저장소 파일 디렉터리와 매핑
    // 브라우저에서 이미지 <img> 태그 요청시 서버 외부 폴더 (./uploads)에 위치한 실제 이미지를 찾아 연결합니다
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String uploadPath = java.nio.file.Paths.get(fileDir).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/images/**").addResourceLocations(uploadPath);

    }
}
