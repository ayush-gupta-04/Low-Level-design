package chain;

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

        int nums = Math.min(amt/valNote, numNote);
        int rem = amt - (nums*valNote);

        if(rem==0) return true;
        if(nexNoteDispenser!=null){
            return nexNoteDispenser.canDispense(rem);
        }
        return false;

    }   
    public synchronized void dispense(int amt){
        if (amt >= valNote) {
            int numToDispense = Math.min(amt / valNote, this.numNote);
            int remainingAmount = amt - (numToDispense * valNote);

            if (numToDispense > 0) {
                System.out.println("Dispensing " + numToDispense + " x $" + valNote + " note(s)");
                this.numNote -= numToDispense;
            }

            if (remainingAmount > 0 && this.nexNoteDispenser != null) {
                this.nexNoteDispenser.dispense(remainingAmount);
            }
        } else if (this.nexNoteDispenser != null) {
            this.nexNoteDispenser.dispense(amt);
        }
    }
    public void setNextNoteDispenser(NoteDispenser noteDispenser){
        this.nexNoteDispenser = noteDispenser;
    }
}
