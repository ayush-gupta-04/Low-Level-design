import java.util.*;


class FlyWeight{
    String state;
    public FlyWeight(String state){
        this.state = state;
    }

    public void render(int posX, int posY){
        System.out.println("State : " + state + " posX : " + posX + " posY : " + posY);
    }

    public static int getMemory(){
        return Character.BYTES*6;
    }
}

class FlyWeightContext{
    int posX;
    int posY;
    FlyWeight fw;
    public FlyWeightContext(String state , int posX ,int posY){
        this.posX = posX;
        this.posY = posY;
        this.fw = FlyWeightFactory.getFlyWeight(state);
    }
    public void render(){
        fw.render(posX,posY);
    }

    public static int getMemory(){
        return 4 + Integer.BYTES*2;
    }
}

class FlyWeightFactory{
    static HashMap<String , FlyWeight> map = new HashMap<>();
    static FlyWeight getFlyWeight(String state){
        if(!map.containsKey(state)){
            map.put(state, new FlyWeight(state));
        }
        return map.get(state);
    }

    public static int getMemory(){
        return map.size()*FlyWeight.getMemory();
    }
}



class Game {
    private List<FlyWeightContext> flyWeights;
    public Game(){
        this.flyWeights = new ArrayList<>();
    }

    public void makeFlyWeights(int cnt){
        String[] states = {"state0" , "state1" , "state2" , "state3"};
        for(int i = 0;i < cnt ;i++){
            int type = i%4;
            flyWeights.add(new FlyWeightContext(states[type], (100 + i*2), (100 + i*3)));
        }
    }

    public long calcMemory(){
        long mem = flyWeights.size()*FlyWeightContext.getMemory() + FlyWeightFactory.getMemory();
        System.out.println();
        return mem;
    }
}


public class Main{
    public static void main(String[] args) {
        Game g = new Game();
        g.makeFlyWeights(100000);
        System.out.println( "Total Memory : " + g.calcMemory());
        System.out.println( "Total Memory in MB : " + g.calcMemory()/(1024.0*1024.0));
    }
}


