package CoffeeVendingMachine;

import java.util.List;
import java.util.Map;

import CoffeeVendingMachine.decorator.CaramelSyrupDecorator;
import CoffeeVendingMachine.decorator.Coffee;
import CoffeeVendingMachine.decorator.ExtraSugarDecorator;
import CoffeeVendingMachine.enums.CoffeeType;
import CoffeeVendingMachine.enums.Ingredient;
import CoffeeVendingMachine.enums.ToppingType;
import CoffeeVendingMachine.factory.CoffeeFactory;
import CoffeeVendingMachine.state.ReadyState;
import CoffeeVendingMachine.state.VendingMachineState;

public class CoffeeVendingMachine {
    private static final CoffeeVendingMachine INSTANCE = new CoffeeVendingMachine();

    private VendingMachineState state;
    private Coffee selectedCoffee;
    private int moneyInserted;

    private CoffeeVendingMachine() {
        this.state = new ReadyState();
        this.moneyInserted = 0;
    }

    public static CoffeeVendingMachine getInstance() {
        return INSTANCE;
    }

    // ------ User Actions ------
    // --- Actions delegated to the current state ---
    public void selectCoffee(CoffeeType type, List<ToppingType> toppings) {

        Coffee coffee = CoffeeFactory.createCoffee(type);
        for (ToppingType topping : toppings) {
            switch (topping) {
                case EXTRA_SUGAR:
                    coffee = new ExtraSugarDecorator(coffee);
                    break;
                case CARAMEL_SYRUP:
                    coffee = new CaramelSyrupDecorator(coffee);
                    break;
                default:
                    break;
            }
        }

        state.selectCoffee(this, coffee);
    }

    public void insertMoney(int amount) {
        state.insertMoney(this, amount);
    }

    public void dispenseCoffee() {
        state.dispenseCoffee(this);
    }

    public void cancel() {
        state.cancel(this);
    }
    // ------ end of user actions --------



    // --- Getters and Setters used by State objects ---
    public void setState(VendingMachineState state) {
        this.state = state;
    }

    public VendingMachineState getState() {
        return state;
    }

    public void setSelectedCoffee(Coffee selectedCoffee) {
        this.selectedCoffee = selectedCoffee;
    }

    public Coffee getSelectedCoffee() {
        return selectedCoffee;
    }

    public void setMoneyInserted(int moneyInserted) {
        if (moneyInserted < 0) {
            return;
        }
        this.moneyInserted = moneyInserted;
    }

    public int getMoneyInserted() {
        return moneyInserted;
    }
    // ---- end of getters and setters -----

    public void reset() {
        this.selectedCoffee = null;
        this.moneyInserted = 0;
    }

    public Map<Ingredient, Integer> showIngredientsMap() {
        return Inventory.getInstance().getAllIngredients();
    }
}
