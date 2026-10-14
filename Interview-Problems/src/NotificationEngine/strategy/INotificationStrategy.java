package NotificationEngine.strategy;

import NotificationEngine.decorator.INotification;

public interface INotificationStrategy {
    void sendNotification(INotification notification);
}