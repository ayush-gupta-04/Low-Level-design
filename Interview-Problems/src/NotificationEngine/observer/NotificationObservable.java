package NotificationEngine.observer;

import java.util.ArrayList;
import java.util.List;

import NotificationEngine.decorator.INotification;

// Concrete Observable
public class NotificationObservable implements IObservable {
    private List<IObserver> observers = new ArrayList<>();

    public void addObserver(IObserver obs) {
        observers.add(obs);
    }

    public void removeObserver(IObserver obs) {
        observers.remove(obs);
    }

    public void notifyObservers(INotification notification) {
        for (IObserver obs : observers) {
            obs.update(notification);
        }
    }
}