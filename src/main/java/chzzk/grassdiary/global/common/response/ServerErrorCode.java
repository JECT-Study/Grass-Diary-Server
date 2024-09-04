package chzzk.grassdiary.global.common.response;

public enum ServerErrorCode implements ErrorCodeModel {
    SERVER_ERR(500, "SERVER_ERR", "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    DB_CONNECTION_ERR(503, "DB_CONNECTION_ERR", "서비스를 이용할 수 없습니다. 나중에 다시 시도해주세요."),
    EXTERNAL_API_ERR(502, "EXTERNAL_API_ERR", "외부 서비스와의 통신에 문제가 발생했습니다. 잠시 후 다시 시도해주세요."),
    OUT_OF_MEMORY(500, "OUT_OF_MEMORY", "서버에 메모리 부족이 발생했습니다. 잠시 후 다시 시도해주세요."),
    NOT_IMPLEMENTED(501, "NOT_IMPLEMENTED", "현재 지원되지 않는 기능입니다."),
    SERVICE_UNAVAILABLE(503, "SERVICE_UNAVAILABLE", "현재 서비스가 사용 불가합니다. 나중에 다시 시도해주세요."),
    QUESTION_UNAVAILABLE(503, "QUESTION_UNAVAILABLE", "현재 '오늘의 일기' 서비스가 사용 불가합니다. 나중에 다시 시도해주세요."),
    IMAGE_UPLOAD_FAILED(500, "IMAGE_UPLOAD_FAILED", "이미지 업로드에 실패했습니다."),
    REWARD_HISTORY_SAVE_FAILED(500, "REWARD_HISTORY_SAVE_FAILED", "일기 히스토리 저장에 실패했습니다.");

    private final int statusCode;
    private final String code;
    private final String message;

    ServerErrorCode(int statusCode, String code, String message) {
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
