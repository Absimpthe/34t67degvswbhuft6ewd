package service;
 
import enums.TicketStatus;
import enums.TicketType;
import model.Ticket;
 
import java.util.ArrayList;
 
public class ReportService {
 
    public void generateReport(ArrayList<Ticket> tickets) {
        System.out.println("\n==========================================");
        System.out.println("           SYSTEM SALES REPORT            ");
        System.out.println("==========================================");
 
        // guard clause: handles both a null list and an empty one
        if (tickets == null || tickets.isEmpty()) {
            System.out.println("No ticket data available to report.");
            System.out.println("==========================================");
            return;
        }
 
        printSalesSummary(tickets);
        printTypeBreakdown(tickets);
 
        System.out.println("==========================================");
    }
 
    private void printSalesSummary(ArrayList<Ticket> tickets) {
        int active = 0;
        int used = 0;
        int cancelled = 0;
        double revenue = 0.0;
        double refunded = 0.0;
 
        for (Ticket ticket : tickets) {
            TicketStatus status = ticket.getStatus();
 
            if (status == TicketStatus.ACTIVE) {
                active++;
                revenue += ticket.getFareAmount();
            } else if (status == TicketStatus.USED) {
                used++;
                revenue += ticket.getFareAmount();
            } else if (status == TicketStatus.CANCELLED) {
                cancelled++;
                refunded += ticket.getFareAmount();
            }
        }
 
        System.out.println("\n--- Ticket Summary ---");
        System.out.println("Total Tickets Issued : " + tickets.size());
        System.out.println("Active               : " + active);
        System.out.println("Used                 : " + used);
        System.out.println("Cancelled            : " + cancelled);
 
        System.out.println("\n--- Revenue ---");
        System.out.printf("Total Revenue        : RM %.2f%n", revenue);
        System.out.printf("Total Refunded       : RM %.2f%n", refunded);
 
        // average is only meaningful over tickets that actually earned money
        int paidTickets = active + used;
        if (paidTickets > 0) {
            System.out.printf("Average Fare         : RM %.2f%n", revenue / paidTickets);
        }
    }
 
    private void printTypeBreakdown(ArrayList<Ticket> tickets) {
        System.out.println("\n--- Breakdown by Ticket Type ---");
 
        for (TicketType type : TicketType.values()) {
            int count = 0;
            double typeRevenue = 0.0;
 
            for (Ticket ticket : tickets) {
                if (ticket.getTicketType() == type) {
                    count++;
                    // cancelled tickets were refunded, so they earn nothing
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
            System.out.printf("%-25s : %2d ticket(s)  |  RM %.2f spent%n",
                    seenIds.get(i), counts.get(i), spend.get(i));
        }
        System.out.println("=====================================");
    }
}
