package payment;
//using enum to "lock" it
public enum EWalletProvider { 
  
    TOUCH_N_GO("Touch 'n Go eWallet"),
    GRABPAY("GrabPay"),
    BOOST("Boost"),
    SHOPEEPAY("ShopePay");

    private final String label;
    //label name for provider
    EWalletProvider(String label){
        this.label = label;
    }
    //return display name
    public String getLabel(){
        return label;
    }
    // return display label
    @Override
    public String toString(){
        return label;
    }
}
