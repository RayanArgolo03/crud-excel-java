package exceptions;

import java.io.Serial;

public class ServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5234310257467979205L;

    public ServiceException(String message) {
        super(message);
    }
}
