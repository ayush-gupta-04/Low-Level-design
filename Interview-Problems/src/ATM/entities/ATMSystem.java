package entities;
import java.util.Optional;

import chain.NoteDispenser;
import chain.NoteDispenser100;
import chain.NoteDispenser50;
import chain.NoteDispenser20;
import enums.OperationType;
import state.ATMState;
import state.IdleState;


public class ATMSystem {
    private static ATMSystem instance;
    private Card currentCard;
    private ATMState currentState;
    private BankService bankService;
    private CashDispenser cashDispenser;

    private ATMSystem(){
        this.currentState = new IdleState();
        this.currentCard = null;
        this.bankService = BankService.getInstance();
        NoteDispenser c1 = new NoteDispenser100(10);
        NoteDispenser c2 = new NoteDispenser50(20);
        NoteDispenser c3 = new NoteDispenser20(30);

        c1.setNextNoteDispenser(c2);
        c2.setNextNoteDispenser(c3);

        this.cashDispenser = new CashDispenser(c1);

    }
    public static ATMSystem getInstance(){
        if(ATMSystem.instance==null){
            synchronized(ATMSystem.class){
                if(ATMSystem.instance==null){
                    ATMSystem.instance = new ATMSystem();
                }
            }
        }
        return ATMSystem.instance;
    }
    public void setCurrentState(ATMState atmState){
        this.currentState = atmState;
    }
    
    // ---- user functions -----
    public void insertCard(String cardNumber){
        this.currentState.insertCard(cardNumber, this);
    };
    public void enterPin(String pin){
        this.currentState.enterPin(pin, this);
    };
    public void selectOperation(OperationType op , int...args){
        this.currentState.selectOperation(op, this, args);
    };
    public void ejectCard(){
        this.currentState.ejectCard(this);
    };

    // --- core functions -----
    public void withdrawCash(int amt){
        if(!cashDispenser.canDispenseCash(amt)){
            System.out.println("Insufficient cash available in the ATM.");
            return;
        }

        bankService.withdraw(currentCard, amt);

        try {
            cashDispenser.dispenseCash(amt);
        } catch (Exception e) {
            bankService.deposite(currentCard, amt);
        }

    }
    public void depositCash(int amt){
        bankService.deposite(currentCard, amt);
    }
    public void checkBalance(){
        double amt = bankService.checkBalance(currentCard);
        System.out.println("Your current balance is : " + amt);
    }
    public boolean authenticate(String pin){
        return bankService.authenticate(currentCard, pin);
    }

    // getters & setters
    public Optional<Card> getCard(String cardNumber){
        return bankService.getCard(cardNumber);
    }
    public void setCurrentCard(Card card){
        this.currentCard = card;
    }
    public Card getCurrentCard(){
        return this.currentCard;
    }
    public BankService getBankService(){
        return bankService;
    }
}
