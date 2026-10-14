package VendingMachine.entities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Inventory {
    private Map<String, Item> items;
    private Map<String, Integer> stock;

    public Inventory(){
        this.items = new ConcurrentHashMap<>();
        this.stock = new ConcurrentHashMap<>();
    }

    // main inventory logic
    public synchronized void addItem(Item item, int qty){
        if(!items.containsKey(item.getCode())){
            items.put(item.getCode(), item);
        }
        stock.put(item.getCode(), stock.getOrDefault(item.getCode(),0) + qty);
    }
    public synchronized boolean tryReduceItem(String code){
        if(!isAvailable(code)){
            return false;
        }
        stock.put(code, stock.get(code)-1);
        return true;
    }
    public synchronized boolean isAvailable(String code){
        if(!stock.containsKey(code)){
            System.out.println("No Item with code : " + code);
            return false;
        }
        return stock.get(code) > 0;
    }
    // ---- end of inventory main logic -----


    // getter
    public synchronized Item getItem(String code){
        return items.get(code);
    }
    
}
