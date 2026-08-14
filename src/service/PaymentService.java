package service;
import payment.Payment;
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

        if (userChoice.equalsIgnoreCase("Y")) {
            return processPayment(payment, amount, true);
        }

        if (userChoice.equalsIgnoreCase("N")) {
            System.out.println("Payment cancelled.");
            return false;
        }

        System.out.println("Invalid choice. Payment cancelled.");
        return false;

        public boolean processPayment(Payment payment, double amount, boolean confirmed) {
        if (payment == null) {
            System.out.println("No payment method selected.");
            return false;
        }
        if (!confirmed) {
            System.out.println("Payment cancelled.");
            return false;
        }
 
        boolean success = payment.pay(amount);
 
        if (success) {
            System.out.printf("Transaction complete via %s.%n", payment.getMethodName());
        } else {
            System.out.println("Transaction failed. Please choose another payment method.");
        }
        return success;
    }
}
