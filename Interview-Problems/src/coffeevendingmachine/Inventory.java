package CoffeeVendingMachine;

import java.util.EnumMap;
import java.util.Map;

import CoffeeVendingMachine.enums.Ingredient;

public class Inventory {
    private static final Inventory INSTANCE = new Inventory();
    private final Map<Ingredient, Integer> stock = new EnumMap<>(Ingredient.class);

    private Inventory() {
        for (Ingredient ingredient : Ingredient.values()) {
            stock.put(ingredient, 0);
        }
    }

    public static Inventory getInstance() {
        return INSTANCE;
    }

    // ------- Main methods ----------
    public synchronized boolean hasIngredients(Map<Ingredient, Integer> recipe) {
        if (recipe == null || recipe.isEmpty()) {
            return false;
        }

        for (Map.Entry<Ingredient, Integer> entry : recipe.entrySet()) {
            Ingredient ingredient = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity <= 0 || stock.getOrDefault(ingredient, 0) < quantity) {
                return false;
            }
        }
        return true;
    }

    // Checks and deducts a recipe atomically so stock cannot change between the two operations.
    public synchronized boolean tryDeductIngredients(Map<Ingredient, Integer> recipe) {
        if (!hasIngredients(recipe)) {
            return false;
        }

        for (Map.Entry<Ingredient, Integer> entry : recipe.entrySet()) {
            Ingredient ingredient = entry.getKey();
            int quantity = entry.getValue();
            stock.put(ingredient, stock.get(ingredient) - quantity);
        }
        return true;
    }
    // ------- End of Main Methods ---------


    public synchronized void addStock(Ingredient ingredient, int quantity) {
        if (ingredient == null || quantity <= 0) {
            return;
        }
        stock.put(ingredient, stock.getOrDefault(ingredient, 0) + quantity);
    }

    // Returns a snapshot so callers cannot modify the machine's internal stock.
    public synchronized Map<Ingredient, Integer> getAllIngredients() {
        return new EnumMap<>(stock);
    }

    public synchronized void printInventory() {
        System.out.println("--- Current Inventory ---");
        stock.forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println("-------------------------");
    }
}
