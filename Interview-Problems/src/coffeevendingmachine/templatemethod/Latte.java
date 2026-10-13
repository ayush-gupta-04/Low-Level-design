package coffeevendingmachine.templatemethod;

import coffeevendingmachine.decorator.Coffee;
import coffeevendingmachine.enums.Ingredient;

import java.util.Map;

public class Latte extends Coffee {
    int price;
    Map<Ingredient, Integer> recipe;

    public Latte() {
        this.price = 250;
        this.coffeeType = "Cappuccino";
        recipe = Map.of(Ingredient.COFFEE_BEANS, 7, Ingredient.WATER, 30, Ingredient.MILK, 150);
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


