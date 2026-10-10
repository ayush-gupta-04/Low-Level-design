package state;

import entities.Coin;
import vendingmachine.VendingMachine;

public interface VendingMachineState {
    public void selectItem(String itemCode, VendingMachine vm);
    public void insertCoin(Coin coin,  VendingMachine vm);
    public void dispense(VendingMachine vm);
    public void refund(VendingMachine vm);
}