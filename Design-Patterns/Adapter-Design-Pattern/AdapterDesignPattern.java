import java.util.*;

interface ITarget{
    public String strToBinary(String str);
}

class Adaptee{
    String intToBinStr(int num){
        return Integer.toBinaryString(num);
    }
}

class Adapter implements ITarget{
    Adaptee ad;
    public Adapter(Adaptee ad){
        this.ad = ad;
    }
    public String strToBinary(String str){
        int num = Integer.parseInt(str);
        String binString = ad.intToBinStr(num);
        return binString;
    }
}

public class Main {
    public static void main(String[] args) {
        ITarget t = new Adapter(new Adaptee());
        System.out.println(t.strToBinary("6"));
    }
}
