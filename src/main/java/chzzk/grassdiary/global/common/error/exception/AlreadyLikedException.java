package chzzk.grassdiary.global.common.error.exception;

public class AlreadyLikedException extends RuntimeException {
    public AlreadyLikedException() {
    }

    public AlreadyLikedException(String message) {
        super(message);
    }

    public AlreadyLikedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyLikedException(Throwable cause) {
        super(cause);
    }
}