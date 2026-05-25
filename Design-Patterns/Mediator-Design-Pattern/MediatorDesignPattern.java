import java.util.*;

public class Main{
    public static void main(String[] args) {
        IMediator mediator = new ChatMediator();
        User ayush = new ChatUser("Ayush", mediator);
        User ishan = new ChatUser("Ishan", mediator);
        User aman = new ChatUser("Aman", mediator);
        User tedha = new ChatUser("Tedha", mediator);
        
        mediator.register(ayush);
        mediator.register(ishan);
        mediator.register(aman);
        mediator.register(tedha);
        ayush.sendTo("Hey! langta","Aman");
    }
}





interface IMediator{
    public void register(User u);
    public void sendAll(String msg , String sender);
    public void sendTo(String msg, String sender, String receiver);
}

class ChatMediator implements IMediator{
    List<User> users;
    public ChatMediator(){
        this.users = new ArrayList<>();
    }

    @Override
    public void register(User u) {
        users.add(u);
    }
    @Override
    public void sendAll(String msg, String sender) {
        for(User u : users){
            if(u.getName() == sender) continue;
            u.receive(msg , sender);
        }
    }

    @Override
    public void sendTo(String msg, String sender, String receiver) {
        for(User u : users){
            if(u.getName() == receiver){
                u.receive(msg, sender);
            }
        }
    }
}

abstract class User{
    IMediator mediator;
    String name;

    User(String name, IMediator mediator){
        this.name = name;
        this.mediator = mediator;
    }
    public String getName(){
        return this.name;
    }
    abstract void send(String msg);
    abstract void receive(String msg , String from);
    abstract void sendTo(String msg , String receiver);
}

class ChatUser extends User{
    ChatUser(String name , IMediator mediator){
        super(name, mediator);
    }

    @Override
    void send(String msg) {
        System.out.println(this.name + " sending msg to all ... ");
        mediator.sendAll(msg, this.name);
    }
    @Override
    void sendTo(String msg, String receiver) {
        System.out.println(this.name + " sending msg to " + receiver);
        mediator.sendTo(msg, this.name, receiver);
    }
    @Override
    void receive(String msg , String from) {
        System.out.println(this.name + " received msg from " + from + " : " + msg);
    }
}
