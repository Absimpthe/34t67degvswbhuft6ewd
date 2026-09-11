package payment;

// simulates paying from an external e-wallet account (e.g. Touch 'n Go) to top up the metro wallet
public class EWalletPayment implements Payment {
    private EWalletProvider provider;
    private String email;   // email address linked to the e-wallet account

    public EWalletPayment(EWalletProvider provider, String email) {
        this.provider = provider;
        this.email = email;
    }

    @Override
    public boolean pay(double amount) {
        if (provider == null) {
            System.out.println("E-Wallet payment failed: no provider selected.");
            return false;
        }
        // safety net: Main already re-prompts until the email is valid
        if (email == null || !email.trim().matches("[^@\\s,]+@[^@\\s,]+\\.[^@\\s,]+")) {
            System.out.println("E-Wallet payment failed: invalid e-wallet email address.");
            return false;
        }
        if (amount <= 0) {
            System.out.println("E-Wallet payment failed: amount must be greater than RM0.00");
            return false;
        }

        System.out.println("Connecting to " + provider.getLabel() + " account " + email + "...");
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

    public String getEmail() {
        return email;
    }
}