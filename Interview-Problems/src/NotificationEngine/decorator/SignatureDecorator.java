package NotificationEngine.decorator;

// Decorator to append a signature to the content.
public class SignatureDecorator extends INotificationDecorator {
    private String signature;

    public SignatureDecorator(INotification n, String sig) {
        super(n);
        this.signature = sig;
    }

    public String getContent() {
        return super.notification.getContent() + "\n-- " + signature + "\n\n";
    }
}
