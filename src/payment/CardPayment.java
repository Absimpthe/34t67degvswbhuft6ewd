package payment;

public class CardPayment implements Payment{
    //card number is private, so it is protected from changes
    private String cardNumber;
    
    public String getCardNumber(){
      return cardNumber;
    }
    
    public CardPayment(String cardNumber){
      this.cardNumber = cardNumber;
    }  
    //validate card first, then changes, or otherwise
    @Override
    public boolean pay(double amount) {
        //card validation
        if(!isValidCardNumber(cardNumber)){
           System.out.println("The card is not verified / Invalid card number. Payment Failed");
            return false;
        }  
        if(amount <= 0 ){
            System.out.println("Card payment failed: amount must be greater than RM0.00");
            return false;
        }
        
        System.out.printf("Processing card payment of RM %.2f...%n", amount);
        System.out.printf("Payment amount: RM %.2f%n", amount);
        System.out.println("Card payment successful.");
        return true;
    }

    public static boolean isValidCardNumber(String cardNumber){
        return cardNumber != null && cardNumber.matches("\\d{14,19}");
    }
    
    @Override 
    public String getMethodName(){
        return "Card";
    }
}
