package state;

import entities.Coin;
import vendingmachine.VendingMachine;

public class ItemSelectedState implements VendingMachineState {
    public void selectItem(String itemCode, VendingMachine vm){
        System.out.println("Item already selected!");
    }
    public void insertCoin(Coin coin,  VendingMachine vm){
        vm.addBalance(coin.getValue());
        System.out.println("Coin Inserted : " + coin.getValue());
        if(vm.getBalance() >= vm.getSelectedItem().getPrice()){
            System.out.println("Sufficient Balance Received!");
            vm.setCurrentState(new HasMoneyState());
        }
    }
    public void dispense(VendingMachine vm){
        System.out.println("Please insert sufficient money!");
    }
    public void refund(VendingMachine vm){
        vm.refundBalance();
        vm.reset();
    }
}