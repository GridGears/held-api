package at.gridgears.held;

public class HeldException extends Exception {
    HeldException(String errorType, String message) {
        super(errorType + ": " + message);
    }

    HeldException(String errorType, Throwable cause) {
        super(errorType, cause);
    }


}