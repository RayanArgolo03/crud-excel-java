package exceptions;

import java.io.Serial;

public class ControllerException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5234310257467979205L;

    public ControllerException(String message) {
        super(message);
    }
}
