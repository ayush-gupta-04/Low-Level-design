package entities;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;

import pubsub.Subscriber;

public class Topic {
    String name;
    HashSet<Subscriber> subcribers;
    ExecutorService executor;

    public Topic(String topicName, ExecutorService executor){
        this.name = topicName;
        this.subcribers = new HashSet<>();
        this.executor = executor;
    }

    public void addSubcriber(Subscriber subscriber){
        subcribers.add(subscriber);
    }

    public void removeSubcriber(Subscriber subscriber){
        subcribers.remove(subscriber);
    }
    public void broadcast(Message msg){
        for(Subscriber s : subcribers){
            executor.submit(() -> {
                try {
                    s.onMessage(msg);
                } catch (Exception e) {
                    System.out.println("Error delivering message!");
                }
            });
        }
    }

}
