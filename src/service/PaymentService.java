package service;
import payment.Payment;


public class PaymentService {

    private int successfulTransactions = 0;
    private int failedTransactions = 0;
    private double totalCollected = 0.0;

    public boolean processPayment(Payment payment, double amount) {
        if (payment == null) {
            System.out.println("No payment method selected.");
            failedTransactions++;
            return false;
        }

        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            failedTransactions++;
            return false;
        }

        boolean success = payment.pay(amount);   // dynamic dispatch

        if (success) {
            successfulTransactions++;
            totalCollected += amount;
            System.out.printf("Transaction complete via %s.%n", payment.getMethodName());
        } else {
            failedTransactions++;
            System.out.println("Transaction failed. Please choose another payment method.");
        }
        return success;
    }
    

    public int getSuccessfulTransactions() {
        return successfulTransactions;
    }

    public int getFailedTransactions() {
        return failedTransactions;
    }

    public double getTotalCollected() {
        return totalCollected;
    }
}
