package at.gridgears.held.internal;

public class HeldException extends Exception {
    HeldException(String errorType, String message) {
        super(errorType + ": " + message);
    }
}