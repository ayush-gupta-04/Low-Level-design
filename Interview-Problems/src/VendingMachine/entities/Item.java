package entities;


public class Item {
    String code;
    String name;
    int price;
    public Item(String code, String name, int price){
        this.code = code;
        this.name = name;
        this.price = price;
    }

    // getter
    public String getCode(){
        return this.code;
    }
    public String getName(){
        return this.name;
    }
    public int getPrice(){
        return this.price;
    }
}
