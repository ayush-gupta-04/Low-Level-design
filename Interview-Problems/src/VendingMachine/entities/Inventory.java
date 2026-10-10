package entities;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    Map<String, Item> items;
    Map<String, Integer> stock;

    public Inventory(){
        this.items = new HashMap<>();
        this.stock = new HashMap<>();
    }

    public void addItem(Item item, int qty){
        if(!items.containsKey(item.getCode())){
            items.put(item.getCode(), item);
        }
        stock.put(item.getCode(), stock.getOrDefault(item.getCode(),0) + qty);
    }
    public void reduceItem(String code){
        stock.put(code, stock.get(code)-1);
    }


    // getter
    public Item getItem(String code){
        return items.get(code);
    }
    public boolean isAvailable(String code){
        return stock.get(code) > 0;
    }
}
