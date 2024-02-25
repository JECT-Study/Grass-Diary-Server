package chzzk.grassdiary.domain.auth.client;

import chzzk.grassdiary.domain.auth.config.GoogleOAuthProperties;
import chzzk.grassdiary.domain.auth.service.dto.GoogleOAuthToken;
import chzzk.grassdiary.domain.auth.service.dto.GoogleUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 스프링 서버가 구글 서버에 인가 코드를 전달하여 액세스 토큰을 받는다.
 */
@Component
@Slf4j
public class GoogleOAuthClient implements OAuthClient {
    private static final String OAUTH_GRANT_TYPE = "authorization_code";
    private final GoogleOAuthProperties googleOAuthProperties;
    private final RestTemplate restTemplate;

    public GoogleOAuthClient(GoogleOAuthProperties googleOAuthProperties, RestTemplateBuilder restTemplateBuilder) {
        this.googleOAuthProperties = googleOAuthProperties;
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * @param code     인가 코드
     * @param isSignUp 회원가입 여부
     * @return 구글 서버에서 발급한 액세스 토큰 (= 사용자 정보를 요청하기 위한 액세스 토큰)
     */
    @Override
    public GoogleOAuthToken getGoogleAccessToken(String code) {
        HttpHeaders httpHeaders = createGoogleAccessRequestHeaders();

        MultiValueMap<String, String> httpBody = createBody(code);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpBody, httpHeaders);
        ResponseEntity<GoogleOAuthToken> googleOAuthTokenResponseEntity = restTemplate.postForEntity(
                googleOAuthProperties.getTokenUri(), request, GoogleOAuthToken.class);

        log.info("=======> 구글로부터 받은 토큰: {}", googleOAuthTokenResponseEntity.getBody());
        return googleOAuthTokenResponseEntity.getBody();
    }

    /**
     * @param accessToken 구글 서버로부터 발급받은 액세스 토큰
     * @return 구글 서버로부터 받은 사용자 정보
     */
    @Override
    public GoogleUserInfo getGoogleUserInfo(String accessToken) {
        HttpHeaders httpHeaders = createUserInfoRequestHeaders(accessToken);

        GoogleUserInfo body = restTemplate.exchange(
                        googleOAuthProperties.getUserInfoUri(),
                        HttpMethod.GET,
                        new HttpEntity<>(httpHeaders),
                        GoogleUserInfo.class)
                .getBody();

        log.info("=====> 구글로부터 받아온 사용자 정보: id - {}, 이메일 - {},  닉네임 - {}, 사진 - {}",
                body.id(), body.email(), body.nickname(), body.picture());

        return body;
    }

    private HttpHeaders createGoogleAccessRequestHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return httpHeaders;
    }

    private MultiValueMap<String, String> createBody(String code) {
        MultiValueMap<String, String> httpBody = new LinkedMultiValueMap<>();
        httpBody.add("code", code);
        httpBody.add("client_id", googleOAuthProperties.getClientId());
        httpBody.add("client_secret", googleOAuthProperties.getClientSecret());
        httpBody.add("redirect_uri", googleOAuthProperties.getRedirectUri());
        httpBody.add("grant_type", OAUTH_GRANT_TYPE);
        return httpBody;
    }

    private HttpHeaders createUserInfoRequestHeaders(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        return httpHeaders;
    }
}
