

import entities.Message;
import pubsub.AlertSubscriber;
import pubsub.PrintSubscriber;
import pubsub.Subscriber;

public class Main {
    public static void main(String[] args) {
        PubSubSystem pub = PubSubSystem.getInstance();

        // create topic
        pub.createTopic("alert");
        pub.createTopic("payment");

        // create subcriber
        Subscriber as1 = new AlertSubscriber();
        Subscriber as2 = new AlertSubscriber();


        Subscriber ps1 = new PrintSubscriber();
        Subscriber ps2 = new PrintSubscriber();

        // subcribe
        pub.subcribe("alert", as1);
        pub.subcribe("alert", as2);
        pub.subcribe("payment", ps1);
        pub.subcribe("payment", ps2);

        pub.publish("alert", new Message("System is Attacked"));

        pub.unsubcribe("alert", as1);
        pub.publish("alert", new Message("System is Attacked again"));

        pub.shutdown();
    }
}