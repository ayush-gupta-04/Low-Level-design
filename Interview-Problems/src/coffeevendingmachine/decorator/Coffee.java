package CoffeeVendingMachine.decorator;

import java.util.Map;

import CoffeeVendingMachine.enums.Ingredient;


// Methods needs to be Decorated :
// 1. prepare.
// 2. getPrice()
// 3. getRecipe()
// 4. getCoffeeType()


public abstract class Coffee {
    protected String coffeeType = "Unknown Coffee";

    public String getCoffeeType() {
        return coffeeType;
    }

    // ---- Template Method ------
    // prepare the base coffee. The machine prints the overall
    public void prepare() {
        grindBeans();
        brew();
        addCondiments();   // subclass would implement this.
        pourIntoCup();
    }

    private void grindBeans() {
        System.out.println("- Grinding fresh coffee beans.");
    }

    private void brew() {
        System.out.println("- Brewing coffee with hot water.");
    }

    protected abstract void addCondiments();  // subclass would implement this.

    private void pourIntoCup() {
        System.out.println("- Pouring into a cup.");
    }
    // ----- end of template method ------------



    public abstract int getPrice();

    public abstract Map<Ingredient, Integer> getRecipe();
}
