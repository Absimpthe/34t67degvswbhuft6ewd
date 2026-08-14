package payment;

public class CardPayment implements Payment{
    private String cardNumber;

    public String getCardNumber(){
      return cardNumber;
    }
    public CardPayment(String cardNumber){
      this.cardNumber = cardNumber;
    }  
    
    @Override
    public boolean pay(double amount) {
        if(cardNumber == null || cardNumber.trim().length() < 14){
           System.out.print("The card is not verified / Invalid card number. Payment Failed");
            return false;
        }  
        System.out.printf("Processing card payment of RM %.2f...%n", amount);
        System.out.println("Payment amount: RM " + amount);
        System.out.println("Card payment successful.");
        return true;
    }
}
