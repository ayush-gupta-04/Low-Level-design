package CoffeeVendingMachine.templatemethod;

import java.util.Map;

import CoffeeVendingMachine.decorator.Coffee;
import CoffeeVendingMachine.enums.Ingredient;

public class Espresso extends Coffee {
    int price;
    Map<Ingredient, Integer> recipe;

    public Espresso() {
        this.price = 250;
        this.coffeeType = "Cappuccino";
        recipe = Map.of(Ingredient.COFFEE_BEANS, 7, Ingredient.WATER, 30);
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


