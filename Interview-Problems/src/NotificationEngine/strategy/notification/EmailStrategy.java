package NotificationEngine.strategy.notification;

import NotificationEngine.decorator.INotification;
import NotificationEngine.strategy.INotificationStrategy;

public class EmailStrategy implements INotificationStrategy {
    private String emailId;

    public EmailStrategy(String emailId) {
        this.emailId = emailId;
    }

    public void sendNotification(INotification notification) {
        // Simulate the process of sending an email notification, 
        // representing the dispatch of messages to users via email.​
        System.out.println("Sending email Notification to: " + emailId + "\n" + notification.getContent());
    }
}