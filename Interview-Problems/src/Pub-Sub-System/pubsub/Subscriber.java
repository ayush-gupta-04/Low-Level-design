package pubsub;

import entities.Message;

public interface Subscriber {
    public void onMessage(Message msg);
}
