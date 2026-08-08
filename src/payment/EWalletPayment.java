package payment

public EWalletPayment implements Payment{
  public EWalletPayment(Passenger passenger, EWalletProvider provider){
    this.passenger = passenger;
    this.provider = provider;
  }
// i still thinking of what to use for payment pay figure
  public boolean hasSufficientBalance(double amount){
    return passenger != null && passenger.getEwalletBalance() >= amount;
} 
  @Override
  public boolean pay(double amount){
    if(amount <= 0){
      System.out.print(" E-Wallet has invalid amount");
    }

    if(!hasSufficientBalance(amount)){
      System.out.printf(" E-Wallet Payment declined. Required RM %.2f, avaiable RM %.2f");
      return false;
    }
    // need to inheretance with pasenger
    if(pas
    System.out.println(" E-Wallet connecting to " + provider + "...");
        boolean ok = passenger.deductEwallet(amount);
        if (ok) {
            System.out.printf("  [E-WALLET] RM %.2f paid. Remaining %s balance RM %.2f.%n",
                    amount, provider, passenger.getEwalletBalance());
        }
        
    return ok;
    
    @Override
    public String getMethodName(){
      return "E-Wallet (" + provider.getlabel() + ")";
    }

    public EWalletProvider getProvider() {
      return provider;
    }

}
