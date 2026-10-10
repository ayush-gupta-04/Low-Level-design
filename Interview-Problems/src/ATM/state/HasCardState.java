package state;

import entities.ATMSystem;
import enums.OperationType;

public class HasCardState implements ATMState {
    public void insertCard(String cardNumber, ATMSystem atm){
        System.out.println("Card already inserted!");
    }
    public void enterPin(String pin, ATMSystem atm){
        System.out.println("Authenticating PIN...");
        boolean isAuth = atm.authenticate(pin);
        if(isAuth){
            System.out.println("Authentication Successful!");
            atm.setCurrentState(new AuthenticatedState());
        }else{
            System.out.println("Authentication failed: Incorrect PIN.");
            ejectCard(atm);
        }
    }
    public void selectOperation(OperationType op, ATMSystem atm, int... args){
        System.out.println("Not authenticated yet!");
    }
    public void ejectCard(ATMSystem atm){
        System.out.println("Card has been ejected. Thank you for using our ATM.");
        atm.setCurrentCard(null);
        atm.setCurrentState(new IdleState());
    }
}
