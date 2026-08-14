package payment;
import model.passenger;

public class EWalletPayment implements Payment {
    private Passenger passenger;
    private EWalletProvider provider;

    public EWalletPayment(Passenger passenger, EWalletProvider provider) {
        this.passenger = passenger;
        this.provider = provider;
    }

    public boolean hasSufficientBalance(double amount) {
        return passenger != null && passenger.getEwalletBalance() >= amount;
    }

    @Override
    public boolean pay(double amount) {
        if (amount <= 0) {
            System.out.println("E-Wallet: invalid amount.");
            return false;
        }
        if (passenger == null) {
            System.out.println("E-Wallet: no passenger linked.");
            return false;
        }
        if (!hasSufficientBalance(amount)) {
            System.out.printf("E-Wallet payment declined. Required RM %.2f, available RM %.2f%n",
                    amount, passenger.getEwalletBalance());
            return false;
        }
        System.out.println("E-Wallet connecting to " + provider + "...");
        boolean okPaid = passenger.deductEwallet(amount);
        if (okPaid) {
            System.out.printf("[E-WALLET] RM %.2f paid. Remaining balance RM %.2f%n",
                    amount, passenger.getEwalletBalance());
        }
        return ok;
    }

    public EWalletProvider getProvider() {
        return provider;
    }
}
