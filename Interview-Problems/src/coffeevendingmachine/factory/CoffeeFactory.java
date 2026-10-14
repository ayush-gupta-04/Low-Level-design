package CoffeeVendingMachine.factory;

import CoffeeVendingMachine.decorator.Coffee;
import CoffeeVendingMachine.enums.CoffeeType;
import CoffeeVendingMachine.templatemethod.Cappuccino;
import CoffeeVendingMachine.templatemethod.Espresso;
import CoffeeVendingMachine.templatemethod.Latte;

public class CoffeeFactory {
    public static Coffee createCoffee(CoffeeType type) {
        if (type == null) {
            throw new IllegalArgumentException("Coffee type cannot be null.");
        }

        switch (type) {
            case ESPRESSO:
                return new Espresso();
            case LATTE:
                return new Latte();
            case CAPPUCCINO:
                return new Cappuccino();
            default:
                throw new IllegalArgumentException("Unsupported coffee type: " + type);
        }
    }
}
