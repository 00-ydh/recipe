package net.likelion.bebc25.recipe.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginMember") == null) {
            log.info("로그인 안된 사용자의 요청: " + requestURI);
            // 미인증 사용자일 경우 로그인 페이지로 리다이렉트
            response.sendRedirect("/member/login");
            return false;
        }

        return true;
    }
}
