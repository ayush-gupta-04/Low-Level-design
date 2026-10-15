package ObserverDesignPattern.PushBased;

import java.util.ArrayList;
import java.util.List;

// 1. Observer interface
interface Observer {
    void update(double temperature);
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

    @Override
    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
        notifyObservers();
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : new ArrayList<>(observers)) {
            // Push updated data directly to the Observer
            observer.update(temperature);
        }
    }
}

// 4. Concrete Observer
class MobileDisplay implements Observer {

    @Override
    public void update(double temperature) {
        System.out.println("Mobile Display: " + temperature + "°C");
    }
}

// 5. Main class
public class Main {
    public static void main(String[] args) {
        WeatherStation station = new WeatherStation();

        Observer mobile = new MobileDisplay();
        station.subscribe(mobile);

        station.setTemperature(25.0);
        station.setTemperature(30.0);

        station.unsubscribe(mobile);
    }
}