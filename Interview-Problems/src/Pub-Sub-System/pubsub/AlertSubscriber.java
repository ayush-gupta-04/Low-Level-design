package pubsub;

import java.util.UUID;

import entities.Message;

public class AlertSubscriber implements Subscriber  {
    String id;
    public AlertSubscriber(){
        this.id = UUID.randomUUID().toString();
    }
    public void onMessage(Message msg){
        System.out.println("Alerting... : " + this.id);
        System.out.println(msg.getPayload());
    }
}
