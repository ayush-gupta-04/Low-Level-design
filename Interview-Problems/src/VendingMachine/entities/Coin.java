package entities;

public enum Coin {
    FIVE(5),
    TEN(10),
    FIVETEEN(15),
    TWENTY(20);

    private final int value;
    Coin(int value){
        this.value = value;
    }
    public int getValue(){
        return this.value;
    }
}
