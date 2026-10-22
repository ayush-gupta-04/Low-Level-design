package ATMSystem.chain;

public abstract class NoteDispenser {
    int valNote;
    int numNote;
    NoteDispenser nexNoteDispenser;

    public NoteDispenser(int val, int num){
        this.valNote = val;
        this.numNote = num;
    }   

    public synchronized boolean canDispense(int amt){
        if(amt < 0) return false;
        if(amt == 0) return true;

        int numToDispense = Math.min(amt / valNote, numNote);
        int remainingAmount = amt - (numToDispense * valNote);

        if(remainingAmount == 0) return true;
        if(nexNoteDispenser != null){
            return nexNoteDispenser.canDispense(remainingAmount);
        }
        return false;
    }   

    public synchronized void dispense(int amt){
        if(amt <= 0) return;

        // Exactly mirror the greedy calculation from canDispense
        int numToDispense = Math.min(amt / valNote, this.numNote);
        int remainingAmount = amt - (numToDispense * valNote);

        if (numToDispense > 0) {
            System.out.println("Dispensing " + numToDispense + " x $" + valNote + " note(s)");
            this.numNote -= numToDispense;
        }

        // Forward whatever is left over to the next chain link
        if (remainingAmount > 0 && this.nexNoteDispenser != null) {
            this.nexNoteDispenser.dispense(remainingAmount);
        }
    }

    public void setNextNoteDispenser(NoteDispenser noteDispenser){
        this.nexNoteDispenser = noteDispenser;
    }
}






// package ATMSystem.chain;

// import java.util.Map;

// public abstract class NoteDispenser {
//     protected final int valNote;
//     protected int numNote;
//     protected NoteDispenser nexNoteDispenser;

//     public NoteDispenser(int val, int num) {
//         this.valNote = val;
//         this.numNote = num;
//     }

//     public void setNextNoteDispenser(NoteDispenser noteDispenser) {
//         this.nexNoteDispenser = noteDispenser;
//     }

//     public synchronized boolean canDispense(int amt, Map<Integer, Integer> plan) {
//         if (amt == 0) return true;

//         // Calculate the maximum number of notes this dispenser can theoretically provide
//         int maxNotes = Math.min(amt / valNote, this.numNote);

//         for (int i = maxNotes; i >= 0; i--) {
//             int remainder = amt - (i * valNote);
            
//             if (remainder == 0) {
//                 if (i > 0) plan.put(valNote, i);
//                 return true;
//             }
            
//             // Forward the remainder to the next dispenser in the chain
//             if (nexNoteDispenser != null && nexNoteDispenser.canDispense(remainder, plan)) {
//                 if (i > 0) plan.put(valNote, i);
//                 return true;
//             }
//         }
        
//         return false;
//     }

//     public synchronized void dispense(Map<Integer, Integer> plan) {
//         if (plan.containsKey(this.valNote)) {
//             int countToDeduct = plan.get(this.valNote);
//             this.numNote -= countToDeduct;
//             System.out.println("Dispensing " + countToDeduct + " x $" + valNote + " note(s). [Remaining stock: " + this.numNote + "]");
//         }
//         if (this.nexNoteDispenser != null) {
//             this.nexNoteDispenser.dispense(plan);
//         }
//         return;
//     }
// }
