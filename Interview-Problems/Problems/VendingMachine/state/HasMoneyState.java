package state;

import entities.Coin;
import vendingmachine.VendingMachine;

public class HasMoneyState implements VendingMachineState {
    public void selectItem(String itemCode, VendingMachine vm){
        System.out.println("Item already selected!");
    }
    public void insertCoin(Coin coin,  VendingMachine vm){
        System.out.println("Already received full amount.");
    }
    public void dispense(VendingMachine vm){
        vm.setCurrentState(new DispensingState());
        vm.dispenseItem();
    }
    public void refund(VendingMachine vm){
        vm.refundBalance();
        vm.reset();
    }
}
