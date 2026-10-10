package state;

import java.util.Optional;

import entities.ATMSystem;
import entities.Card;
import enums.OperationType;

public class IdleState implements ATMState {
    public void insertCard(String cardNumber, ATMSystem atm){
        Optional<Card> card = atm.getCard(cardNumber);
        if(card.isPresent()){
            atm.setCurrentCard(card.get());
            atm.setCurrentState(new HasCardState());
            return;
        }
    }
    public void enterPin(String pin, ATMSystem atm){
        System.out.println("Please insert a card first!");
    }
    public void selectOperation(OperationType op, ATMSystem atm, int... args){
        System.out.println("Please insert a card first!");
    }
    public void ejectCard(ATMSystem atm){
        System.out.println("Error: card not found");
        atm.setCurrentCard(null);
    }
}
