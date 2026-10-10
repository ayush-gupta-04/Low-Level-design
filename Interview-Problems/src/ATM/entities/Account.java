package entities;

import java.util.HashMap;

public class Account {
    String accNumber;
    double balance;
    HashMap<String, Card> cards;

    public Account(String accNumber, double initBalance){
        this.accNumber = accNumber;
        this.balance = initBalance;
        this.cards  = new HashMap<>();
    }

    public synchronized void deposit(double amt){
        balance += amt;
    }
    public synchronized void withdraw(double amt){
        if(balance >= amt){
            balance -= amt;
        }
    }
    public void addCard(Card card){
        cards.put(card.getCardNumber(), card);
    }

    // getter
    public String getAccountNumber(){
        return this.accNumber;
    }
    public synchronized double getBalance(){
        return this.balance;
    }
}
