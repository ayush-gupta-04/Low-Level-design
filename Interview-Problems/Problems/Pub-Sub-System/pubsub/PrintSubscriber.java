package pubsub;

import java.util.UUID;

import entities.Message;

public class PrintSubscriber implements Subscriber {
    String id;
    public PrintSubscriber(){
        this.id = UUID.randomUUID().toString();
    }
    public void onMessage(Message msg){
        System.out.println("Printing ... : " + this.id);
        System.out.println(msg.getPayload());
    }
}
