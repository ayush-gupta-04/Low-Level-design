import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 1. Create a Prototype Interface 🧬
interface Prototype {
    Monster clone();
}

// 2. The nested object (The Backpack 🎒)
class Inventory {
    List<String> items;

    // Standard constructor
    public Inventory(List<String> items) {
        this.items = new ArrayList<>(items);
    }

    // COPY CONSTRUCTOR
    public Inventory(Inventory target) {
        if (target != null) {
            this.items = new ArrayList<>(target.items); 
        }
    }
}

// 3. The Concrete Prototype
class Monster implements Prototype {
    String name;
    Inventory inventory;   // will require deep copy !

    // normal constructor.
    public Monster(String name, Inventory inventory) {
        this.name = name;
        this.inventory = inventory;
    }

    // copy constructor.
    public Monster(Monster other){
        this.inventory = new Inventory(other.inventory);   // making a deep copy of this inventory.
        this.name = other.name;
    }

    @Override
    public Monster clone() {
        return new Monster(this);
    }
}

public class PrototypeExample {
    public static void main(String[] args) {
        // Create our original prototype
        Inventory originalInventory = new Inventory(Arrays.asList("Sword", "Shield"));
        Monster bossMonster = new Monster("Orc King", originalInventory);

        // Clone it!
        Monster clonedMonster = bossMonster.clone();

        // The clone finds a new item!
        clonedMonster.inventory.items.add("Magic Potion");

        System.out.println("Original items: " + bossMonster.inventory.items); 
        // Output: [Sword, Shield]

        System.out.println("Cloned items:   " + clonedMonster.inventory.items); 
        // Output: [Sword, Shield, Magic Potion]
    }
}
