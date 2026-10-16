package MeetingScheduler.service;

import java.util.HashMap;
import java.util.Map;

import MeetingScheduler.entities.User;

public class UserService {
    private Map<String, User> users;

    public UserService(){
        this.users = new HashMap<>();
    }

    public void addUser(User user){
        users.putIfAbsent(user.getId(), user);
    }
    public void removeUser(String id){
        if(!users.containsKey(id)) return;
        users.remove(id);
    }
    public User getUser(String id){
        return users.get(id);
    }
}
