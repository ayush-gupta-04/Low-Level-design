package ObserverDesignPattern.PullBased;

import java.util.ArrayList;
import java.util.List;

// 1. Observer interface
interface Observer {
    void update();
}

// 2. Subject interface
interface Subject {
    void subscribe(Observer observer);
    void unsubscribe(Observer observer);
    void notifyObservers();
}

// 3. Concrete Subject
class WeatherStation implements Subject {
    private final List<Observer> observers = new ArrayList<>();
    private double temperature;

    public synchronized double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        synchronized (this) {
            this.temperature = temperature;
        }
        notifyObservers();
    }

    @Override
    public synchronized void subscribe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public synchronized void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}

// 4. Concrete Observer
class MobileDisplay implements Observer {

    private final WeatherStation station;

    public MobileDisplay(WeatherStation station) {
        this.station = station;
    }

    @Override
    public void update() {
        // Pull the latest temperature from the Subject
        double temperature = station.getTemperature();
        System.out.println("Mobile Display: " + temperature + "°C");
    }
}

// 5. Main class
public class Main {
    public static void main(String[] args) {
        WeatherStation station = new WeatherStation();

        Observer mobile = new MobileDisplay(station);
        station.subscribe(mobile);

        station.setTemperature(25.0);
        station.setTemperature(30.0);

        station.unsubscribe(mobile);
    }
}