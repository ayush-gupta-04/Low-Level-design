package vendingmachine;
import entities.Coin;
import entities.Inventory;
import entities.Item;
import state.IdleState;
import state.VendingMachineState;

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

    // states methods
    public void selectItem(String itemCode){
        currentState.selectItem(itemCode, this);
    }
    public void insertCoin(Coin coin){
        currentState.insertCoin(coin, this);
    }
    public void dispense(){
        currentState.dispense(this);
    }
    public void refund(){
        currentState.refund(this);
    }



    // imp functions
    public void addItem(String code, String name, int price , int qty){
        Item item = new Item(code, name, price);
        inventory.addItem(item, qty);
    }
    public void dispenseItem(){
        Item item = inventory.getItem(selectedItemCode);
        if(balance >= item.getPrice()){
            inventory.reduceItem(selectedItemCode);
            balance -= item.getPrice();
            
            System.out.println("Dispensed : " + item.getName());
            if(balance > 0){
                System.out.println("Returning Exchange : " + balance);
            }
        }
        this.reset();
    }
    public void reset(){
        this.balance = 0;
        this.selectedItemCode = null;
        this.currentState = new IdleState();
        System.out.println("Reset Successfully!");
    }
    public void refundBalance(){
        System.out.println("Refunding Balance : " + balance);
        balance = 0;
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
