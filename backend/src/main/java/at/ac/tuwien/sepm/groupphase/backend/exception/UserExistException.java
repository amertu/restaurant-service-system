package at.ac.tuwien.sepm.groupphase.backend.exception;

public class UserExistException extends RuntimeException {
    public UserExistException() {
    }

    public UserExistException(String message) {
        super(message);
    }

    public UserExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserExistException(Exception e) {
        super(e);
    }
}