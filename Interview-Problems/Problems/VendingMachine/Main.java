import entities.Coin;
import vendingmachine.VendingMachine;

public class Main {
    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        vm.addItem("D-1", "pepsi", 20, 2);
        vm.addItem("D-2", "coke", 25, 3);

        vm.selectItem("D-1");
        vm.insertCoin(Coin.FIVE);
        vm.insertCoin(Coin.FIVETEEN);
        vm.dispense();

        vm.selectItem("D-2");
        vm.insertCoin(Coin.TEN);
        vm.insertCoin(Coin.FIVETEEN);
        vm.dispense();
    }
}
