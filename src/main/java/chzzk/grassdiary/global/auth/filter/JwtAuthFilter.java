package chzzk.grassdiary.global.auth.filter;

import chzzk.grassdiary.global.auth.jwt.JwtTokenExtractor;
import chzzk.grassdiary.global.auth.jwt.JwtTokenProvider;
import chzzk.grassdiary.domain.member.entity.MemberDAO;
import chzzk.grassdiary.global.common.error.exception.SystemException;
import chzzk.grassdiary.global.common.response.ClientErrorCode;
import chzzk.grassdiary.global.common.response.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenExtractor jwtTokenExtractor;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberDAO memberDAO;
    private final List<String> allowedOrigins = Arrays.asList(
        "https://grassdiary.site",
        "http://localhost:3000"
    );

    private final List<String> publicPaths = List.of(
            "/api/shared/diaries"
    );

    private void setCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        if (allowedOrigins.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers",
                "authorization, content-type, accept, origin, x-requested-with");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        setCorsHeaders(request, response);

        String path = request.getRequestURI();
        if (isPublicPath(path) || request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = jwtTokenExtractor.extractAccessToken(request);
            Long id = jwtTokenProvider.extractIdFromAccessToken(accessToken);
            validateMemberExist(id);
            filterChain.doFilter(request, response);
        } catch (SystemException e) {
            log.error(e.getMessage());
            jwtExceptionHandler(response, e);
        }
    }

    private boolean isPublicPath(String path) {
        return publicPaths.stream().anyMatch(path::startsWith);
    }

    public void jwtExceptionHandler(HttpServletResponse response, SystemException exception) throws IOException {
        response.setStatus(exception.getErrorCode().getStatusCode());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Response errorResponse = Response.error(exception.getErrorCode());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void validateMemberExist(Long id) {
        if (memberDAO.findById(id).isEmpty()) {
            throw new SystemException(ClientErrorCode.UNAUTHORIZED);
        }
    }
}
