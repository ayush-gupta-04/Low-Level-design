package NotificationEngine.observer;

import NotificationEngine.decorator.INotification;

// Observer interface: each observer gets an update with a Notification pointer.
public interface IObserver {
    void update(INotification notification);
}