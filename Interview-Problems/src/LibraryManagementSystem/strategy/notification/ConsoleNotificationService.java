package LibraryManagementSystem.strategy.notification;

import LibraryManagementSystem.entities.Member;
import LibraryManagementSystem.strategy.NotificationService;

public class ConsoleNotificationService implements NotificationService {
    public void notify(Member member, String message) {
        System.out.println("[NOTIFY -> " + member.getName() + "] " + message);
    }
}
