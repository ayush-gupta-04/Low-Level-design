package NotificationEngine.observer;

import NotificationEngine.decorator.INotification;

public class Logger implements IObserver {
    public void update(INotification notification) {
        System.out.println("Logging New Notification : \n" + notification.getContent());
    }
}
