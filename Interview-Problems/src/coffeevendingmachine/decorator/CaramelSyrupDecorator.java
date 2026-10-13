package coffeevendingmachine.decorator;

import coffeevendingmachine.enums.Ingredient;

import java.util.HashMap;
import java.util.Map;

public class CaramelSyrupDecorator extends CoffeeDecorator {
    private static final int COST = 30;
    private static final Map<Ingredient, Integer> RECIPE_ADDITION = Map.of(Ingredient.CARAMEL_SYRUP, 10);

    public CaramelSyrupDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getCoffeeType() {
        return decoratedCoffee.getCoffeeType() + ", Caramel Syrup";
    }

    @Override
    public int getPrice() {
        return decoratedCoffee.getPrice() + COST;
    }

    @Override
    public Map<Ingredient, Integer> getRecipe() {
        // Merge the recipes
        Map<Ingredient, Integer> newRecipe = new HashMap<>(decoratedCoffee.getRecipe());
        for(Map.Entry<Ingredient, Integer> e : RECIPE_ADDITION.entrySet()){
            newRecipe.put(e.getKey(), newRecipe.getOrDefault(e.getKey(), 0) + e.getValue());
        }
        return newRecipe;
    }

    @Override
    public void prepare() {
        // First, prepare the underlying coffee (e.g., the Latte with Sugar)
        super.prepare();
        // Then, add the specific step for this decorator
        System.out.println("- Drizzling Caramel Syrup on top.");
    }
}
