package at.ac.tuwien.sepm.groupphase.backend.exception;


public class DependentDataException extends RuntimeException {
    public DependentDataException() {
    }

    public DependentDataException(String message) {
        super(message);
    }

    public DependentDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public DependentDataException(Exception e) {
        super(e);
    }
}
