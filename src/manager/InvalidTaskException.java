package manager;

public class InvalidTaskException extends RuntimeException {
    public InvalidTaskException(final String message) {
        super(message);
    }
}
