package coffeevendingmachine.templatemethod;

import coffeevendingmachine.decorator.Coffee;
import coffeevendingmachine.enums.Ingredient;

import java.util.Map;

public class Cappuccino extends Coffee {
    int price;
    Map<Ingredient, Integer> recipe;

    public Cappuccino() {
        this.price = 250;
        this.coffeeType = "Cappuccino";
        recipe = Map.of(Ingredient.COFFEE_BEANS, 7, Ingredient.WATER, 30, Ingredient.MILK, 100);
    }

    @Override
    protected void addCondiments() {
        System.out.println("- Adding steamed milk and foam.");
    }

    @Override
    public int getPrice() {
        return this.price;
    }

    @Override
    public Map<Ingredient, Integer> getRecipe() {
        return recipe;
    }
}
