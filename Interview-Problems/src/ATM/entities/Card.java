package entities;

public class Card {
    String cardNumber;
    String pin;
    public Card(String cardNumber, String pin){
        this.cardNumber = cardNumber;
        this.pin = pin;
    }
    public String getCardNumber(){
        return this.cardNumber;
    }
    public String getPin(){
        return this.pin;
    }
}
