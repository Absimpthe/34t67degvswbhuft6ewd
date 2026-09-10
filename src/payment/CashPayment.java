package payment;

public class CashPayment implements Payment{
  @Override
    public boolean pay(double amount) {
        if(amount <= 0){
          System.out.println("Not valid.");
          return false;
        }
      
        System.out.printf("Processing cash payment of RM %.2f...%n", amount);
        System.out.printf("Payment amount: RM %.2f%n", amount);
        System.out.println("Cash payment successful.");
        return true;
    }


  @Override 
  public String getMethodName(){
      return "Cash";
  }
}
