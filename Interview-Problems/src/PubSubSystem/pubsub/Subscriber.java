package PubSubSystem.pubsub;

import PubSubSystem.entities.Message;

public interface Subscriber {
    public void onMessage(Message msg);
}
