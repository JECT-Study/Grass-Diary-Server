package chzzk.grassdiary.global.common.response;

public enum ClientErrorCode implements ErrorCodeModel {
    UNAUTHORIZED(401, "UNAUTHORIZED", "인증이 필요합니다. 로그인 해주세요."),
    AUTHENTICATION_FAILED(401, "AUTHENTICATION_FAILED", "인증을 실패했습니다."),
    AUTH_INVALID_ACCESS_TOKEN(401, "AUTH_INVALID_ACCESS_TOKEN", "잘못된 액세스 토큰입니다."),
    AUTH_EXPIRED_ACCESS_TOKEN(401, "AUTH_EXPIRED_ACCESS_TOKEN", "액세스 토큰이 만료되었습니다."),
    AUTH_INVALID_SIGNATURE(401, "AUTH_INVALID_SIGNATURE", "잘못된 시그니처입니다."),
    AUTH_JWT_ERROR(401, "AUTH_JWT_ERROR", "JWT 관련 오류가 발생했습니다."),
    AUTH_MISSING_ID_IN_ACCESS_TOKEN(401, "AUTH_MISSING_ID_IN_ACCESS_TOKEN", "JWT 액세스 토큰에 ID가 누락되었습니다."),
    AUTH_TOKEN_EXTRACTION_FAILED(401, "AUTH_TOKEN_EXTRACTION_FAILED", "액세스 토큰 추출에 실패했습니다."),
    AUTH_SESSION_EXPIRED(440, "AUTH_SESSION_EXPIRED", "세션이 만료되었습니다. 다시 로그인 해주세요."),

    MEMBER_NOT_FOUND_ERR(404, "MEMBER_NOT_FOUND_ERR", "요청하신 사용자를 찾을 수 없습니다."),
    PAST_DIARY_MODIFICATION_NOT_ALLOWED(409, "PAST_DIARY_MODIFICATION_NOT_ALLOWED", "과거의 일기는 수정할 수 없습니다."),
    DIARY_NOT_FOUND_ERR(404, "DIARY_NOT_FOUND_ERR", "요청하신 다이어리를 찾을 수 없습니다."),
    DIARY_LIKE_NOT_FOUND(404, "DIARY_LIKE_NOT_FOUND", "해당 다이어리에 좋아요를 누르지 않았습니다."),
    DIARY_LIKE_ALREADY_EXISTS(409, "DIARY_LIKE_ALREADY_EXISTS", "다이어리에 좋아요를 이미 눌렀습니다."),
    DIARY_ALREADY_EXISTS_FOR_TODAY(409, "DIARY_ALREADY_EXISTS_FOR_TODAY", "일기는 하루에 하나만 작성 가능합니다."),
    COMMENT_NOT_FOUND_ERR(404, "COMMENT_NOT_FOUND_ERR", "요청하신 댓글을 찾을 수 없습니다."),
    INVALID_IMAGE_FORMAT(400, "INVALID_IMAGE_FORMAT", "허용되지 않는 파일 형식입니다."),
    IMAGE_FILE_EMPTY(400, "IMAGE_FILE_EMPTY", "이미지 파일이 비어 있습니다."),
    IMAGE_NOT_FOUND_ERR(400, "IMAGE_NOT_FOUND_ERR", "해당 이미지를 찾을 수 없습니다.(이미지가 저장되어 있지 않음)"),
    IMAGE_MAPPING_NOT_FOUND_ERR(400, "IMAGE_MAPPING_NOT_FOUND_ERR", "해당 이미지를 찾을 수 없습니다.(이미지 등록 값 없음)"),
    AUTHOR_MISMATCH_ERR(403, "AUTHOR_MISMATCH_ERR", "작성자가 아닙니다."),
    COMMENT_ALREADY_DELETED_ERR(400, "COMMENT_ALREADY_DELETED_ERR", "이미 삭제된 댓글입니다."),
    COMMENT_DEPTH_EXCEEDED_ERR(400, "COMMENT_DEPTH_EXCEEDED_ERR", "댓글은 대댓글까지만 허용합니다."),

    VALIDATION_ERR(400, "VALIDATION_ERR", "잘못된 입력입니다. 올바른 값을 입력해주세요."),
    PERMISSION_ERR(403, "PERMISSION_ERR", "접근 권한이 없습니다. 관리자에게 문의하세요."),
    TIMEOUT_ERR(408, "TIMEOUT_ERR", "요청 시간이 초과되었습니다. 다시 시도해주세요."),
    ILLEGAL_ACCESS(400, "ILLEGAL_ACCESS", "잘못된 요청입니다. 올바른 경로로 접근해주세요."),
    BAD_REQUEST_ERR(400, "BAD_REQUEST_ERR", "요청 형식이 잘못되었습니다. 올바른 형식으로 요청해주세요."),
    UNSUPPORTED_MEDIA_ERR(415, "UNSUPPORTED_MEDIA_ERR", "지원되지 않는 미디어 타입입니다. 올바른 형식으로 요청해주세요."),
    MISSING_PARAM_ERR(400, "MISSING_PARAM_ERR", "필수 파라미터가 누락되었습니다. 모든 필드를 입력해주세요."),
    CONFLICT_ERR(409, "CONFLICT_ERR", "데이터 충돌이 발생했습니다. 요청을 다시 확인해주세요."),
    PASSWORD_POLICY_ERR(400, "PASSWORD_POLICY_ERR", "비밀번호가 정책을 위반합니다. 다른 비밀번호를 사용해주세요."),
    RATE_LIMIT_EXCEEDED(429, "RATE_LIMIT_EXCEEDED", "API 요청 한도를 초과했습니다. 잠시 후 다시 시도해주세요."),
    ACCOUNT_LOCKED(423, "ACCOUNT_LOCKED", "사용자 계정이 잠겼습니다. 관리자에게 문의하세요.");


    private final int statusCode;
    private final String code;
    private final String message;

    ClientErrorCode(int statusCode, String code, String message) {
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public String getSystemErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMessage() {
        return this.message;
    }
}
