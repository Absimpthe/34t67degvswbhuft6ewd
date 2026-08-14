package model;

import enums.UserRole;

public class Passenger extends User {

    private double balance;

    public Passenger(String userId,String name,String email,String password,double balance) {
        //父类User
        super(userId, name, email, password, UserRole.PASSENGER);
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    //provides controlled access to modify the private balance attribute.
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void topUp(double amount) {

        if (amount > 0) {
            balance += amount;
            System.out.println("Top up successful.");
        } 
        else {
            System.out.println("Invalid amount.");
        }

    }

}
