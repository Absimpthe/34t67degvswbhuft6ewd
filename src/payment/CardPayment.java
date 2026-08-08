package payment;

public class CardPayment implements Payment{
    private double cardNumber;

    public double getCardNumber{
      return cardNumber;
    }
    public CardPayment(double cardNumber){
      this.cardNumber = cardNumber;
    }  
    
    @Override
    public boolean pay(double amount) {
        if(cardNumber == null // cardNumber.trim().length() < 14){
           System.out.print("The card is not verified / Invalid card number". Payment Failed");
            return false;
        }  
        System.out.printf("Processing cash payment of RM %.2f...%n", amount);
        System.out.println("Payment amount: RM " + amount);
        System.out.println("Cash payment successful.");
        return true;
    }
}
