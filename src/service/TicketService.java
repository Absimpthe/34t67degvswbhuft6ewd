package service;

import enums.TicketStatus;
import enums.TicketType;
import exception.TicketNotFoundException;
import fare.FareCalculator;
import fare.StandardFareCalculator;
import model.Passenger;
import model.Route;
import model.Ticket;

import java.util.ArrayList;

// manage all ticket operations (Buy, Cancel, View)
public class TicketService {

    private ArrayList<Ticket> tickets;
    private FareCalculator fareCalculator;
    private int ticketCounter;

    // the constructor initializing ticket list & starting ID counter
    public TicketService() {
        this.tickets = new ArrayList<>();
        this.fareCalculator = new StandardFareCalculator();
        this.ticketCounter = 1001; // auto generate ID starts at T1001
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
        updateTicketCounter();
    }

    // method to handle buying a new ticket
    public Ticket buyTicket(Passenger passenger, Route route, TicketType ticketType) {
        // here calc fare using FareCalculator
        double fare = fareCalculator.calculateFare(route, ticketType);

        // checking if passenger has enough bal
        if (passenger.getBalance() < fare) {
            System.out.println("Purchase failed: Insufficient balance in wallet.");
            System.out.println("Required: RM " + String.format("%.2f", fare) + " | Available: RM " + String.format("%.2f", passenger.getBalance()));
            return null;
        }

        // minus payment amt from passenger bal
        passenger.setBalance(passenger.getBalance() - fare);

        // create new ticket obj & add it to list
        String ticketId = "T" + ticketCounter++;
        Ticket newTicket = new Ticket(ticketId, passenger, route, ticketType, TicketStatus.ACTIVE, fare);
        tickets.add(newTicket);

        System.out.println("Ticket purchased successfully!");
        newTicket.displayTicket();

        return newTicket;
    }

    // to cancel an existing ticket
    public void cancelTicket(String ticketId) throws TicketNotFoundException {
        Ticket targetTicket = null;

        // here it loops through tickets list to find the matching Ticket ID
        for (int i = 0; i < tickets.size(); i++) {
            Ticket t = tickets.get(i);
            if (t.getTicketId().equalsIgnoreCase(ticketId)) {
                targetTicket = t;
                break;
            }
        }

        // throw the exception if ticket ID doesn't exist
        if (targetTicket == null) {
            throw new TicketNotFoundException("Error: Ticket ID " + ticketId + " was not found.");
        }

        // first check if ticket was already cancelled
        if (targetTicket.getStatus() == TicketStatus.CANCELLED) {
            System.out.println("Ticket ID " + ticketId + " is already cancelled.");
            return;
        }

        // then return money back to passenger balance
        Passenger passenger = targetTicket.getPassenger();
        passenger.setBalance(passenger.getBalance() + targetTicket.getFareAmount());

        // updates ticket status to CANCELLED
        targetTicket.setStatus(TicketStatus.CANCELLED);
        System.out.println("Ticket ID " + ticketId + " has been successfully cancelled.");
        System.out.println("Refund of RM " + String.format("%.2f", targetTicket.getFareAmount()) + " has been credited back to passenger balance.");
    }

    // to view all the tickets booked by a passenger
    public void viewPassengerTickets(String userId) {
        boolean found = false;
        System.out.println("\n=== Your Tickets ===");

        for (int i = 0; i < tickets.size(); i++) {
            Ticket t = tickets.get(i);
            if (t.getPassenger().getUserId().equalsIgnoreCase(userId)) {
                t.displayTicket();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No tickets found for User ID: " + userId);
        }
    }

    // to view all tickets stored in system (Admin view)
    public void viewAllTickets() {
        if (tickets.isEmpty()) {
            System.out.println("No tickets recorded in the system.");
            return;
        }

        System.out.println("\n=== All System Tickets ===");
        for (int i = 0; i < tickets.size(); i++) {
            tickets.get(i).displayTicket();
        }
    }
    
    private void updateTicketCounter() {
        int max = 1000;

        for (Ticket ticket : tickets) {
            String id = ticket.getTicketId();
            if (id != null && id.startsWith("T")) {
                try {
                    int value = Integer.parseInt(id.substring(1));
                    if (value > max) {
                        max = value;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        this.ticketCounter = max + 1;
    }
}
