package CoffeeVendingMachine.state;

import CoffeeVendingMachine.CoffeeVendingMachine;
import CoffeeVendingMachine.Inventory;
import CoffeeVendingMachine.decorator.Coffee;

public class PaidState implements VendingMachineState {
    @Override
    public void selectCoffee(CoffeeVendingMachine machine, Coffee coffee) {
        System.out.println("Cannot select another coffee now.");
    }

    @Override
    public void insertMoney(CoffeeVendingMachine machine, int amount) {
        System.out.println("Already paid. Please wait for your coffee.");
    }

    @Override
    public void dispenseCoffee(CoffeeVendingMachine machine) {
        Coffee coffeeToDispense = machine.getSelectedCoffee();
        Inventory inventory = Inventory.getInstance();

        // Check and deduct stock in one atomic operation.
        if (!inventory.tryDeductIngredients(coffeeToDispense.getRecipe())) {
            System.out.println("Sorry, insufficient ingredients for " + coffeeToDispense.getCoffeeType() + ".");
            System.out.println("Refunding " + machine.getMoneyInserted());
            machine.reset();
            machine.setState(new ReadyState());
            return;
        }

        System.out.println("\nPreparing your " + coffeeToDispense.getCoffeeType() + "...");
        coffeeToDispense.prepare();
        System.out.println(coffeeToDispense.getCoffeeType() + " is ready!");

        int change = machine.getMoneyInserted() - coffeeToDispense.getPrice();
        if (change > 0) {
            System.out.println("Returning change: " + change);
        }

        machine.reset();
        machine.setState(new ReadyState());
    }

    @Override
    public void cancel(CoffeeVendingMachine machine) {
        new SelectedState().cancel(machine);
    }
}
