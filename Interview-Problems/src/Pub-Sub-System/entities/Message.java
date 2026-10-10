package entities;

import java.util.UUID;

public class Message {
    String id;
    String payload;
    
    public Message(String payload){
        this.id = UUID.randomUUID().toString();
        this.payload = payload;
    }   

    public String getPayload(){
        return this.payload;
    }
}
