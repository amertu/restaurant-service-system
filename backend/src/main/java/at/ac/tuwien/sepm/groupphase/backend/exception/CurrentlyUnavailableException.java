package at.ac.tuwien.sepm.groupphase.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE)
public class CurrentlyUnavailableException extends RuntimeException {

    public CurrentlyUnavailableException() {
    }

    public CurrentlyUnavailableException(String message) {
        super(message);
    }

    public CurrentlyUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrentlyUnavailableException(Exception e) {
        super(e);
    }
}

