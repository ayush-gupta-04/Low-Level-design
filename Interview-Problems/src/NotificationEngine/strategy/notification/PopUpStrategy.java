package NotificationEngine.strategy.notification;

import NotificationEngine.decorator.INotification;
import NotificationEngine.strategy.INotificationStrategy;

public class PopUpStrategy implements INotificationStrategy {
    public void sendNotification(INotification notification) {
        // Simulate the process of sending popup notification.
        System.out.println("Sending Popup Notification: \n" + notification.getContent());
    }
}
