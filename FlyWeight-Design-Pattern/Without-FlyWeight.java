import java.util.*;


class FlyWeight{
    String state;
    int posX;
    int posY;
    public FlyWeight(String state , int posX ,int posY){
        this.state = state;
        this.posX = posX;
        this.posY = posY;
    }

    public void render(int posX, int posY){
        System.out.println("State : " + state + " posX : " + posX + " posY : " + posY);
    }

    public static int getMemory(){
        return Character.BYTES*20 + Integer.BYTES*2;
    }
}




class Game {
    private List<FlyWeight> flyWeights;
    public Game(){
        this.flyWeights = new ArrayList<>();
    }

    public void makeFlyWeights(int cnt){
        String[] states = {"state0" , "state1" , "state2" , "state3"};
        for(int i = 0;i < cnt ;i++){
            int type = i%4;
            flyWeights.add(new FlyWeight(states[type], (100 + i*2), (100 + i*3)));
        }
    }

    public long calcMemory(){
        long mem = flyWeights.size()*FlyWeight.getMemory();
        return mem;
    }
}


public class Main{
    public static void main(String[] args) {
        Game g = new Game();
        g.makeFlyWeights(1_000_000);
        System.out.println( "Total Memory : " + g.calcMemory());
        System.out.println( "Total Memory in MB : " + g.calcMemory()/(1024.0*1024.0));
    }
}


