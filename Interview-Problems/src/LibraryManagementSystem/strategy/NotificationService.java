package LibraryManagementSystem.strategy;

import LibraryManagementSystem.entities.Member;

public interface NotificationService {
    public void notify(Member member, String message);
}

