package CoffeeVendingMachine.decorator;

import java.util.HashMap;
import java.util.Map;

import CoffeeVendingMachine.enums.Ingredient;

public class ExtraSugarDecorator extends CoffeeDecorator {
    private static final int COST = 10;
    private static final Map<Ingredient, Integer> RECIPE_ADDITION = Map.of(Ingredient.SUGAR, 1);

    public ExtraSugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getCoffeeType() {
        return decoratedCoffee.getCoffeeType() + ", Extra Sugar";
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
        super.prepare();
        System.out.println("- Stirring in Extra Sugar.");
    }
}
