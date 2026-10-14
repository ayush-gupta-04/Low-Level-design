package NotificationEngine.observer;

import NotificationEngine.decorator.INotification;

public interface IObservable {
    void addObserver(IObserver observer);
    void removeObserver(IObserver observer);
    void notifyObservers(INotification notification);
}
