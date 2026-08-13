package model;

import enums.TicketStatus;
import enums.TicketType;

// holds all info related to a single ticket
public class Ticket {

    private String ticketId;
    private Passenger passenger;
    private Route route;
    private TicketType ticketType;
    private TicketStatus status;
    private double fareAmount;

    // default constructor
    public Ticket() {
    }

    // parameter constructor to initialize a new Ticket
    public Ticket(String ticketId, Passenger passenger, Route route, TicketType ticketType, TicketStatus status, double fareAmount) {
        this.ticketId = ticketId;
        this.passenger = passenger;
        this.route = route;
        this.ticketType = ticketType;
        this.status = status;
        this.fareAmount = fareAmount;
    }

    // get methods
    public String getTicketId() {
        return ticketId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Route getRoute() {
        return route;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public double getFareAmount() {
        return fareAmount;
    }

    // set methods
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public void setFareAmount(double fareAmount) {
        this.fareAmount = fareAmount;
    }

    // display ticket detail format
    public void displayTicket() {
        System.out.println("--- Ticket Information ---");
        System.out.println("Ticket ID    : " + ticketId);
        System.out.println("Passenger    : " + passenger.getName());
        System.out.println("From Station : " + route.getSource().getName());
        System.out.println("To Station   : " + route.getDestination().getName());
        System.out.println("Ticket Type  : " + ticketType);
        System.out.println("Status       : " + status);
        System.out.println("Fare Amount  : RM " + String.format("%.2f", fareAmount));
        System.out.println("--------------------------");
    }

    // convert ticket details to CSV format for file saving
    @Override
    public String toString() {
        return ticketId + "," + passenger.getUserId() + "," + route.getRouteId() + "," + ticketType + "," + status + "," + fareAmount;
    }
}
