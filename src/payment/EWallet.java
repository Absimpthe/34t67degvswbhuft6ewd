package payment

public EWallet implements Payment{
  @Override
  public boolean pay(double amount){
    if(amount <= 0){
      System.out.print(" E-Wallet has invalid amount");
    }
    if(





}
