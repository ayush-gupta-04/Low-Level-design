package VendingMachine.vendingmachine;

import VendingMachine.state.IdleState;
import VendingMachine.state.VendingMachineState;
import VendingMachine.entities.Coin;
import VendingMachine.entities.Inventory;
import VendingMachine.entities.Item;

public class VendingMachine {
    VendingMachineState currentState;
    Inventory inventory;
    int balance;
    String selectedItemCode = null;

    public VendingMachine(){
        this.balance = 0;
        this.inventory = new Inventory();
        this.currentState = new IdleState();
    }
    public void setCurrentState(VendingMachineState currentState) {
        this.currentState = currentState;
    }

    // ------ states methods -------
    // I must serialize the operations, since 2 threads on one vending machine can cause issues.
    public synchronized void selectItem(String itemCode){
        currentState.selectItem(itemCode, this);
    }
    public synchronized void insertCoin(Coin coin){
        currentState.insertCoin(coin, this);
    }
    public synchronized void dispense(){
        currentState.dispense(this);
    }
    public synchronized void refund(){
        currentState.refund(this);
    }



    // imp functions
    public void dispenseItem() {
        Item item = inventory.getItem(selectedItemCode);

        if (item == null) {
            System.out.println("Item not found!");
            refundBalance();
            reset();
            return;
        }

        if (balance < item.getPrice()) {
            System.out.println("Insufficient balance!");
            return;
        }

        // Atomically check and decrement stock.
        if (!inventory.tryReduceItem(selectedItemCode)) {
            System.out.println("Item is no longer available!");
            refundBalance();
            reset();
            return;
        }

        // Charge only after successfully reducing stock.
        balance -= item.getPrice();

        System.out.println("Dispensed : " + item.getName());

        if (balance > 0) {
            System.out.println("Returning Exchange : " + balance);
        }
        reset();
    }

    public void reset(){
        this.balance = 0;
        this.selectedItemCode = null;
        this.currentState = new IdleState();
        System.out.println("Reset Successfully!");
    }
    public void refundBalance(){    
        System.out.println("Refunding Balance : " + balance);
        this.balance = 0;
    }


    // helper function
    public void addItem(String code, String name, int price , int qty){
        Item item = new Item(code, name, price);
        inventory.addItem(item, qty);
    }


    // getter & setter
    public int getBalance(){
        return this.balance;
    }
    public Item getSelectedItem(){
        return inventory.getItem(selectedItemCode);
    }
    public Inventory getInventory(){
        return this.inventory;
    }
    public void setSelectedItemCode(String code){
        this.selectedItemCode = code;
    }
    public void addBalance(int balance){
        this.balance += balance;
    }
}
