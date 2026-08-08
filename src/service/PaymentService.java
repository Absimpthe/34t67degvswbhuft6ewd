package service;
import java.util.*;

public class PaymentService {
  public boolean processPayment(Payment payment, double amount) {
        Scanner choice = new Scanner(System.in);
        if (payment == null) {
            System.out.println("No payment method selected.");
            return false;
        }

        System.out.println("Would you like to proceed with the payment? (Y/N)");
        String userChoice = choice.nextLine().toUpperCase();
        if(userChoice.equals("N")) {
            System.out.println("Payment cancelled.");
            return false;
        } else if(!userChoice.equals("Y")) {
            System.out.println("Invalid choice. Payment cancelled.");
            return false;
        }
        return payment.pay(amount);
    }
}
