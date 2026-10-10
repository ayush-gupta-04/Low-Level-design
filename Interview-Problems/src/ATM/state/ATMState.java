package state;

import entities.ATMSystem;
import enums.OperationType;

public interface ATMState {
    public void insertCard(String cardNumber, ATMSystem atm);
    public void enterPin(String pin, ATMSystem atm);
    public void selectOperation(OperationType op, ATMSystem atm, int... args);
    public void ejectCard(ATMSystem atm);
}
