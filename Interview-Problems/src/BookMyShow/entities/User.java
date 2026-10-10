package entities;

import java.util.UUID;

public class User {
    String id;
    String name;
    String email;
    public User(String name, String email){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }

    public String getId(){return this.id;}
    public String getName(){return this.name;}
    public String getEmail(){return this.email;}
}
