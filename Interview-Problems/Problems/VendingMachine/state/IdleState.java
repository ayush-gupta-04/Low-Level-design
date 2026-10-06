package state;

import entities.Coin;
import vendingmachine.VendingMachine;

public class IdleState implements VendingMachineState {
    public IdleState(){
        System.out.println("Idle State recovered");
    }
    public void selectItem(String itemCode, VendingMachine vm){
        if(!vm.getInventory().isAvailable(itemCode)){
            System.out.println("Item Not available!");
            return;
        }
        vm.setSelectedItemCode(itemCode);
        vm.setCurrentState(new ItemSelectedState());
        System.out.println("Selected Item Code : " + itemCode);
    }
    public void insertCoin(Coin coin,  VendingMachine vm){
        System.out.println("First Select Item!");
    }
    public void dispense(VendingMachine vm){
        System.out.println("No Item to dispense!");
    }
    public void refund(VendingMachine vm){
        System.out.println("No Balance to refund!");
    }
}