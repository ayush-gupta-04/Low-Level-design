package NotificationEngine.service;

import NotificationEngine.observer.IObserver;
import NotificationEngine.observer.NotificationObservable;

import java.util.ArrayList;
import java.util.List;

import NotificationEngine.decorator.*;

/*============================
       NotificationService
=============================*/

// The NotificationService manages notifications. It keeps track of notifications. 
// Any client code will interact with this service.

// Singleton class
public class NotificationService {
    private NotificationObservable observable;
    private static NotificationService instance = null;
    private List<INotification> notifications = new ArrayList<>();

    private NotificationService() {
        observable = new NotificationObservable();
    }

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    // Expose the observable so observers can attach.
    public NotificationObservable getObservable() {
        return observable;
    }

    // Creates a new Notification and notifies observers.
    public void sendNotification(INotification notification) {
        notifications.add(notification);
        observable.notifyObservers(notification);
    }
    public void addObserver(IObserver observer){
        observable.addObserver(observer);
    }
}