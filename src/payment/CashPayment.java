package payment;

public class CashPayment implements Payment{
  @Override
    public boolean pay(double amount) {
        System.out.printf("Processing cash payment of RM %.2f...%n", amount);
        System.out.println("Payment amount: RM " + amount);
        System.out.println("Cash payment successful.");
        return true;
    }


  @Override 
  public String getMethodName(){
      return "Cash";
  }
}
