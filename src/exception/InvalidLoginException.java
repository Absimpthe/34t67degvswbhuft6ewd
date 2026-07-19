package exception;

/**
 * Thrown when a user attempts to log in with incorrect credentials.
 */
public class InvalidLoginException extends Exception {
    
    // Constructor that accepts a custom error message
    public InvalidLoginException(String message) {
        super(message);
    }
}
