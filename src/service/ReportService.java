package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import model.Ticket;
import enums.TicketStatus;
import enums.TicketType;

public class ReportService {

    private ArrayList<Ticket> tickets;

    public ReportService() {
        this.tickets = new ArrayList<>();
    }

    public void setTickets(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void generateReport(ArrayList<Ticket> tickets) {
        this.tickets = tickets;

        System.out.println("\n==========================================");
        System.out.println("           SYSTEM SALES REPORT            ");
        System.out.println("==========================================");

        if (this.tickets == null || this.tickets.isEmpty()) {
            System.out.println("No ticket data available to report.");
            System.out.println("==========================================");
            return;
        }

        printSalesSummaryHeader();
        showTotalSales();
        showTotalRevenue();
        showCancelledTickets();
        printOtherSalesStats();

        printTypeBreakdown(this.tickets);

        System.out.println("==========================================");
    }

    private void printSalesSummaryHeader() {
        System.out.println("\n--- Ticket Summary ---");
    }

    public void showTotalSales() {
        int active = 0;
        int used = 0;

        for (Ticket ticket : tickets) {
            TicketStatus status = ticket.getStatus();
            if (status == TicketStatus.ACTIVE) {
                active++;
            } else if (status == TicketStatus.USED) {
                used++;
            }
        }

        System.out.println("Active               : " + active);
        System.out.println("Used                 : " + used);
    }

    public void showTotalRevenue() {
        double revenue = 0.0;

        for (Ticket ticket : tickets) {
            TicketStatus status = ticket.getStatus();
            if (status == TicketStatus.ACTIVE || status == TicketStatus.USED) {
                revenue += ticket.getFareAmount();
            }
        }

        System.out.printf("Total Revenue        : RM %.2f%n", revenue);

        int paidTickets = 0;
        for (Ticket ticket : tickets) {
            TicketStatus status = ticket.getStatus();
            if (status == TicketStatus.ACTIVE || status == TicketStatus.USED) {
                paidTickets++;
            }
        }

        if (paidTickets > 0) {
            System.out.printf("Average Fare         : RM %.2f%n", revenue / paidTickets);
        } else {
            System.out.printf("Average Fare         : RM %.2f%n", 0.0);
        }
    }

    public void showCancelledTickets() {
        int cancelled = 0;
        double refunded = 0.0;

        for (Ticket ticket : tickets) {
            if (ticket.getStatus() == TicketStatus.CANCELLED) {
                cancelled++;
                refunded += ticket.getFareAmount();
            }
        }

        System.out.println("Cancelled            : " + cancelled);
        System.out.printf("Total Refunded       : RM %.2f%n", refunded);
    }

    private void printOtherSalesStats() {
        System.out.println("Total Tickets Issued : " + tickets.size());
    }

    private void printTypeBreakdown(ArrayList<Ticket> tickets) {
        System.out.println("\n--- Breakdown by Ticket Type ---");

        for (TicketType type : TicketType.values()) {
            int count = 0;
            double typeRevenue = 0.0;

            for (Ticket ticket : tickets) {
                if (ticket.getTicketType() == type) {
                    count++;
                    if (ticket.getStatus() != TicketStatus.CANCELLED) {
                        typeRevenue += ticket.getFareAmount();
                    }
                }
            }

            System.out.printf("%-10s : %3d ticket(s)  |  RM %.2f%n", type, count, typeRevenue);
        }
    }

    public void generatePassengerReport(ArrayList<Ticket> tickets) {
        System.out.println("\n===== PASSENGER ACTIVITY REPORT =====");

        if (tickets == null || tickets.isEmpty()) {
            System.out.println("No ticket data available.");
            return;
        }

        ArrayList<String> seenIds = new ArrayList<>();
        ArrayList<Integer> counts = new ArrayList<>();
        ArrayList<Double> spend = new ArrayList<>();

        for (Ticket ticket : tickets) {
            String userId = ticket.getPassenger().getUserId();
            int index = seenIds.indexOf(userId);

            if (index == -1) {
                seenIds.add(userId);
                counts.add(1);
                spend.add(ticket.getStatus() == TicketStatus.CANCELLED ? 0.0 : ticket.getFareAmount());
            } else {
                counts.set(index, counts.get(index) + 1);
                if (ticket.getStatus() != TicketStatus.CANCELLED) {
                    spend.set(index, spend.get(index) + ticket.getFareAmount());
                }
            }
        }

        for (int i = 0; i < seenIds.size(); i++) {
            System.out.printf("Passenger %-10s | Tickets: %3d | Spent: RM %.2f%n",
                    seenIds.get(i), counts.get(i), spend.get(i));
        }
    }
}