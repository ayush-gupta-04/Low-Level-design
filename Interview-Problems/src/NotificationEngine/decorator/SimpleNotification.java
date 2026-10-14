package NotificationEngine.decorator;

// Concrete Notification: simple text notification.
public class SimpleNotification implements INotification {
    private String text;

    public SimpleNotification(String msg) {
        this.text = msg;
    }

    public String getContent() {
        return text;
    }
}
