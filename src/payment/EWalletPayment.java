package payment;

// simulates paying from an external e-wallet app (e.g. Touch 'n Go) to top up the metro wallet
public class EWalletPayment implements Payment {
    private EWalletProvider provider;
    private String phoneNumber;

    public EWalletPayment(EWalletProvider provider, String phoneNumber) {
        this.provider = provider;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean pay(double amount) {
        if (provider == null) {
            System.out.println("E-Wallet payment failed: no provider selected.");
            return false;
        }
        // Malaysian mobile number: 01 followed by 8 or 9 digits, e.g. 0123456789
        if (phoneNumber == null || !phoneNumber.trim().matches("01\\d{8,9}")) {
            System.out.println("E-Wallet payment failed: invalid phone number (e.g. 0123456789).");
            return false;
        }
        if (amount <= 0) {
            System.out.println("E-Wallet payment failed: amount must be greater than RM0.00");
            return false;
        }

        System.out.println("Connecting to " + provider.getLabel() + "...");
        System.out.printf("Processing e-wallet payment of RM %.2f...%n", amount);
        System.out.println("E-Wallet payment successful.");
        return true;
    }

    @Override
    public String getMethodName() {
        return (provider == null) ? "E-Wallet" : "E-Wallet (" + provider.getLabel() + ")";
    }

    public EWalletProvider getProvider() {
        return provider;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}