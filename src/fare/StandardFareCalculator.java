package fare;

import enums.TicketType;
import model.Route;

// implement FareCalculator with standard rate logic
public class StandardFareCalculator implements FareCalculator {

    @Override
    public double calculateFare(Route route, TicketType ticketType) {
        double distance = route.getDistanceKm();
        double baseFare = 2.00;  // Base rate RM 2.00
        double ratePerKm = 0.50; // RM 0.50 per kilometer

        // calc basic distance fare
        double calculatedFare = baseFare + (distance * ratePerKm);

        // apply price adjustments based on ticket type
        if (ticketType == TicketType.SINGLE) {
            return calculatedFare;
        } else if (ticketType == TicketType.DAILY) {
            return calculatedFare * 2.5; // here multiplies for daily unlimited pass
        } else if (ticketType == TicketType.MONTHLY) {
            return calculatedFare * 15.0; // here multiplies for monthly pass
        }

        return calculatedFare;
    }
}
