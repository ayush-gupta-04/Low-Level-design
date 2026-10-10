package state;

import entities.Coin;
import vendingmachine.VendingMachine;

public class DispensingState implements VendingMachineState {
    public void selectItem(String itemCode, VendingMachine vm){
        System.out.println("Dispensing Item in Progress!");
    }
    public void insertCoin(Coin coin,  VendingMachine vm){
        System.out.println("Dispensing Item in Progress!");
    }
    public void dispense(VendingMachine vm){
        System.out.println("Dispensing Item in Progress!");
    }
    public void refund(VendingMachine vm){
        System.out.println("Dispensing Item in Progress!");
    }
}
