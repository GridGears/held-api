package at.gridgears.held;

class ResponseParsingException extends Exception {
    ResponseParsingException(String message) {
        super(message);
    }

    ResponseParsingException(String message, Throwable cause) {
        super(message, cause);
    }

}
