package payment

public EWallet implements Payment{
  @Override
  public boolean pay(double amount){
    if(amount <= 0){
      System.out.print(" E-Wallet has invalid amount");
    }
    System.out.println(" E-Wallet connecting to " + provider + "...");
        boolean ok = passenger.deductEwallet(amount);
        if (ok) {
            System.out.printf("  [E-WALLET] RM %.2f paid. Remaining %s balance RM %.2f.%n",
                    amount, provider, passenger.getEwalletBalance());
        }
        return ok;





}
