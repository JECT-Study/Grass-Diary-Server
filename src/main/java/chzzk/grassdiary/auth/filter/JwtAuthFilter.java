package chzzk.grassdiary.auth.filter;

import chzzk.grassdiary.auth.exception.AuthenticationException;
import chzzk.grassdiary.auth.jwt.JwtTokenExtractor;
import chzzk.grassdiary.auth.jwt.JwtTokenProvider;
import chzzk.grassdiary.domain.member.Member;
import chzzk.grassdiary.domain.member.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenExtractor jwtTokenExtractor;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers",
                "authorization, content-type, accept, origin, x-requested-with");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");

        if (!request.getMethod().equals("OPTIONS")) {
            String accessToken = jwtTokenExtractor.extractAccessToken(request);
            Long id = jwtTokenProvider.extractIdFromAccessToken(accessToken);
            validateMemberExist(id);
            filterChain.doFilter(request, response);
        }
    }

    private void validateMemberExist(Long id) {
        Optional<Member> foundMember = memberRepository.findById(id);
        if (foundMember.isEmpty()) {
            String logMessage = "인증 실패(미인증 사용자 요청) - member_id: " + id;
            throw new AuthenticationException(logMessage);
        }
    }
}
