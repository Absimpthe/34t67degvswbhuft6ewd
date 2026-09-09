package exception;

public class TicketNotFoundException extends Exception {
    
    // Constructor that accepts a custom error message
    public TicketNotFoundException(String message) {
        super(message);
    }
}
