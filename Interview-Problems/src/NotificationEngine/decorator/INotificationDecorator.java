package NotificationEngine.decorator;

// Abstract Decorator: wraps a Notification object.
public abstract class INotificationDecorator implements INotification {
    protected INotification notification;

    public INotificationDecorator(INotification n) {
        this.notification = n;
    }
}
