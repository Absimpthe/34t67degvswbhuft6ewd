package payment;
//using enum to "lock" it, if write wrong then compile-error
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
    // overriding toString() to concatenation the string later on to print a full label instead of just "touch 'n go"
    @Override
    public String toString(){
        return label;
    }
}
