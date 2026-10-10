package state;

import enums.OperationType;
import entities.ATMSystem;

public class AuthenticatedState implements ATMState{
    public void insertCard(String cardNumber, ATMSystem atm){
        System.out.println("Error: A card is already inserted and a session is active.");
    }
    public void enterPin(String pin, ATMSystem atm){
        System.out.println("Error: PIN has already been entered and authenticated.");
    }
    public void selectOperation(OperationType op, ATMSystem atm, int... args){
        switch (op) {
            case CHECK_BALANCE:
                atm.checkBalance();
                break;
            case WITHDRAW_CASH:
                if(args.length==0 || args[0] <= 0){
                    System.out.println("Error: Invalid withdrawal amount specified.");
                    break;
                }
                int amtToWithdraw = args[0];
                double balance = atm.getBankService().checkBalance(atm.getCurrentCard());
                if(balance >= amtToWithdraw){
                    System.out.println("Processing withdrawal for $" + amtToWithdraw);
                    atm.withdrawCash(amtToWithdraw);
                    break;
                }
                System.out.println("Insufficient funds!");
                break;
            case DEPOSIT_CASH:
                if(args.length==0 || args[0] <= 0){
                    System.out.println("Error: Invalid withdrawal amount specified.");
                    break;
                }
                int amtToDeposit = args[0];
                System.out.println("Processing deposit for $" + amtToDeposit);
                atm.depositCash(amtToDeposit);
                break;
            default:
                System.out.println("Error: Invalid operation selected.");
                break;
        }

        // End the session after one transaction
        System.out.println("Transaction complete.");
        ejectCard(atm);
    }
    public void ejectCard(ATMSystem atm){
        System.out.println("Ending session. Card has been ejected. Thank you for using our ATM.");
        atm.setCurrentCard(null);
        atm.setCurrentState(new IdleState());
    }
}
