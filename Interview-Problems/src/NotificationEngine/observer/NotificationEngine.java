package NotificationEngine.observer;

import java.util.ArrayList;
import java.util.List;

import NotificationEngine.decorator.INotification;
import NotificationEngine.strategy.INotificationStrategy;

public class NotificationEngine implements IObserver {
    private List<INotificationStrategy> notificationStrategies = new ArrayList<>();

    public void addNotificationStrategy(INotificationStrategy ns) {
        this.notificationStrategies.add(ns);
    }

    public void update(INotification notification) {
        for (INotificationStrategy strategy : notificationStrategies) {
            strategy.sendNotification(notification);
        }
    }
}
