package fare;

import enums.TicketType;
import model.Route;

// calc ticket fare
public interface FareCalculator {
    double calculateFare(Route route, TicketType ticketType);
}
