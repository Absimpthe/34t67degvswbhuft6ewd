package exception;

public class InvalidLoginException extends Exception {
    
    // Constructor that accepts a custom error message
    public InvalidLoginException(String message) {
        super(message);
    }
}
