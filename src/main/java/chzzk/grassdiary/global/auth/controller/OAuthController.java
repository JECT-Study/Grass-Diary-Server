package chzzk.grassdiary.global.auth.controller;

import chzzk.grassdiary.global.auth.service.OAuthService;
import chzzk.grassdiary.global.auth.service.dto.JWTTokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;

    @Value("${client.login-success-redirect-uri}")
    private String loginSuccessUri;

    @GetMapping("/google")
    public void loginGoogle(HttpServletResponse response) throws IOException {
        String redirectUri = oAuthService.findRedirectUri();
        response.sendRedirect(redirectUri);
    }

    @GetMapping("/code/google")
    public void authorizeUser(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        JWTTokenResponse jwtToken = oAuthService.loginGoogle(code);
        
        // JWT 토큰을 쿠키에 저장
        Cookie cookie = new Cookie("accessToken", jwtToken.accessToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 쿠키 전송
        cookie.setPath("/");
        cookie.setMaxAge(3600); // 쿠키 유효 시간 설정 (예: 1시간)
        response.addCookie(cookie);

        response.sendRedirect(loginSuccessUri);
    }
}
