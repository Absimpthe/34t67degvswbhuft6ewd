package exception;

/**
 * Thrown when a requested ticket cannot be found within the data store.
 */
public class TicketNotFoundException extends Exception {
    
    // Constructor that accepts a custom error message
    public TicketNotFoundException(String message) {
        super(message);
    }
}
