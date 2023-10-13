package exceptions;

import java.io.Serial;

public class ClientException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5234310257467979205L;

    public ClientException(String message) {
        super(message);
    }
}
