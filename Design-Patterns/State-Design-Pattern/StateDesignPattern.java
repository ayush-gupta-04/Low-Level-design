import java.util.*;
import java.io.*;


public class Main {
    public static void main(String[] args) {
        Context c = new Context();
        c.fun1(1);
        c.fun1(1);

      // I am in state 1 : 1
      // Trasition into state 2 from State 1
      // I am in State 2 : 
      // Cannot perform this action!
    }
}

class Context{
    State currentState;

    public Context(){
        this.currentState = new ConcreteState1();
    }

    public void changeState(State currentState) {
        this.currentState = currentState;
    }
    
    public void fun1(int a) {
        this.currentState.fun1(a,this);
    }
    public void fun2(int b) {
        this.currentState.fun2(b,this);
    }
    public void fun3(int c) {
        this.currentState.fun3(c,this);
    }
}

interface State{
    public void fun1(int a, Context context);
    public void fun2(int b, Context context);
    public void fun3(int c, Context context);
}


class ConcreteState1 implements State{
    @Override
    public void fun1(int a , Context context) {
        System.out.println("I am in state 1 : " + a);
        if(a==1){
            System.out.println("Trasition into state 2 from State 1");
            State nextState = new ConcreteState2();
            context.changeState(nextState);
        }
    }
    @Override
    public void fun2(int b, Context context) {
        System.out.println("I am in State 1 : ");
        System.out.println("Cannot perform this action!");
    }
    @Override
    public void fun3(int c, Context context) {
        System.out.println("I am in State 1 : ");
        System.out.println("Cannot perform this action!");
    }

}

class ConcreteState2 implements State{

    @Override
    public void fun1(int a, Context context) {
        System.out.println("I am in State 2 : ");
        System.out.println("Cannot perform this action!");
    }
    @Override
    public void fun2(int b, Context context) {
        System.out.println("I am in state 2 : " + b);
        if(b==1){
            System.out.println("Trasition into state 3 from State 2");
            State nextState = new ConcreteState3();
            context.changeState(nextState);
        }
    }
    @Override
    public void fun3(int c, Context context) {
        System.out.println("I am in State 2 : ");
        System.out.println("Cannot perform this action!");
    }
}
class ConcreteState3 implements State{

    @Override
    public void fun1(int a, Context context) {
        System.out.println("I am in State 3 : ");
        System.out.println("Cannot perform this action!");
    }
    @Override
    public void fun2(int b, Context context) {
        System.out.println("I am in State 3 : ");
        System.out.println("Cannot perform this action!");
    }
    @Override
    public void fun3(int c, Context context) {
    System.out.println("I am in state 3 : " + c);
        if(c==1){
            System.out.println("Trasition into state 1 from State 3");
            State nextState = new ConcreteState1();
            context.changeState(nextState);
        }
    }
}

