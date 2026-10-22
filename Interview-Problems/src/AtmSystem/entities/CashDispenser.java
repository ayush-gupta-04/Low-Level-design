package ATMSystem.entities;

import java.util.HashMap;
import java.util.Map;

import ATMSystem.chain.NoteDispenser;

public class CashDispenser {
    NoteDispenser noteDispenser;

    public CashDispenser(NoteDispenser noteDispenser){
        this.noteDispenser = noteDispenser;
    }

    public synchronized boolean tryDispenseCash(int amount) {
        if (amount <= 0 || !noteDispenser.canDispense(amount)) {
            return false;
        }
        noteDispenser.dispense(amount);
        return true;
    }

    // public synchronized boolean tryDispenseCash(int amount) {
    //     if(amount <= 0) return false;

    //     Map<Integer, Integer> resultPlan = new HashMap<>();
        
    //     if (noteDispenser.canDispense(amount, resultPlan)) {
    //         noteDispenser.dispense(resultPlan);
    //         return true;
    //     }
    //     return false;
    // }
}
