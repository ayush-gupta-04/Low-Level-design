import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import entities.Message;
import entities.Topic;
import pubsub.Subscriber;

public class PubSubSystem {
    private static PubSubSystem instance;
    HashMap<String, Topic> topics;
    ExecutorService executor;

    private PubSubSystem(){
        this.topics = new HashMap<>();
        this.executor = Executors.newCachedThreadPool();
    }

    public static PubSubSystem getInstance(){
        if(PubSubSystem.instance == null){
            synchronized(PubSubSystem.class){
                if(instance == null){
                    PubSubSystem.instance = new PubSubSystem();
                }
            }
        }
        return PubSubSystem.instance;
    }

    public void createTopic(String topicName){
        topics.putIfAbsent(topicName, new Topic(topicName, executor));
    }
    public void subcribe(String topicName, Subscriber subcriber){
        if(topics.containsKey(topicName)){
            topics.get(topicName).addSubcriber(subcriber);
        }else{
            System.out.println("Topic not found : " + topicName);
        }
    }
    public void unsubcribe(String topicName, Subscriber subcriber){
        if(topics.containsKey(topicName)){
            topics.get(topicName).removeSubcriber(subcriber);
        }else{
            System.out.println("Topic not found : " + topicName);
        }
        
    }
    public void publish(String topicName , Message message){
        if(topics.containsKey(topicName)){
            topics.get(topicName).broadcast(message);
        }else{
            System.out.println("Topic not found : " + topicName);
        }
    }

    public void shutdown(){
        try {
            if(!executor.awaitTermination(10, TimeUnit.SECONDS)){
                executor.shutdown();
                executor.shutdownNow();
            }
        } catch (Exception e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
