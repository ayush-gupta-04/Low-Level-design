package entities;

import chain.NoteDispenser;

public class CashDispenser {
    NoteDispenser noteDispenser;

    public CashDispenser(NoteDispenser noteDispenser){
        this.noteDispenser = noteDispenser;
    }   

    public boolean canDispenseCash(int amt){
        if(amt % 10 != 0) return false;
        return noteDispenser.canDispense(amt);
    }   
    public void dispenseCash(int amt){
        noteDispenser.dispense(amt);
    }
}
