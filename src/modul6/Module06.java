package modul6;

import java.util.*;

// ======================================================
// ЧАСТЬ 1: ПАТТЕРН STRATEGY (Бронирование путешествий)
// ======================================================

// Параметры поездки (Контейнер для данных)
class TravelDetails {
    double distance;
    int passengers;
    boolean isBusinessClass;
    double discount; // например, 0.1 для 10%

    public TravelDetails(double d, int p, boolean biz, double disc) {
        this.distance = d;
        this.passengers = p;
        this.isBusinessClass = biz;
        this.discount = disc;
    }
}

// 1. Интерфейс стратегии
interface ICostCalculationStrategy {
    double calculate(TravelDetails details);
}

// 2. Конкретные стратегии
class AirplaneStrategy implements ICostCalculationStrategy {
    public double calculate(TravelDetails d) {
        double base = d.distance * 0.5; // 0.5$ за км
        if (d.isBusinessClass) base *= 2.0; // Доплата за бизнес
        return (base * d.passengers) * (1 - d.discount);
    }
}

class TrainStrategy implements ICostCalculationStrategy {
    public double calculate(TravelDetails d) {
        double base = d.distance * 0.2;
        if (d.isBusinessClass) base *= 1.5;
        return (base * d.passengers) * (1 - d.discount);
    }
}

class BusStrategy implements ICostCalculationStrategy {
    public double calculate(TravelDetails d) {
        return (d.distance * 0.1 * d.passengers) * (1 - d.discount);
    }
}

// 3. Контекст
class TravelBookingContext {
    private ICostCalculationStrategy strategy;

    public void setStrategy(ICostCalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public void calculateTrip(TravelDetails details) {
        if (strategy == null) {
            System.out.println("Ошибка: стратегия не выбрана!");
            return;
        }
        double cost = strategy.calculate(details);
        System.out.println("Стоимость поездки: " + cost + "$");
    }
}

// ======================================================
// ЧАСТЬ 2: ПАТТЕРН OBSERVER (Биржа)
// ======================================================

// 1. Интерфейс наблюдателя
interface IObserver {
    void update(String stock, double price);
}

// 2. Интерфейс субъекта
interface ISubject {
    void registerObserver(String stock, modul06DZ.IObserver observer);
    void removeObserver(String stock, modul06DZ.IObserver observer);
    void notifyObservers(String stock, double price);
}

// 3. Класс Биржи (Субъект)
class StockExchange implements modul06DZ.ISubject {
    // Храним список подписчиков для каждой акции отдельно
    private Map<String, List<modul06DZ.IObserver>> observers = new HashMap<>();

    public void registerObserver(String stock, modul06DZ.IObserver observer) {
        observers.computeIfAbsent(stock, k -> new ArrayList<>()).add(observer);
        System.out.println("Система: Добавлен подписчик на акцию " + stock);
    }

    public void removeObserver(String stock, modul06DZ.IObserver observer) {
        if (observers.containsKey(stock)) {
            observers.get(stock).remove(observer);
        }
    }

    public void notifyObservers(String stock, double price) {
        if (observers.containsKey(stock)) {
            for (modul06DZ.IObserver observer : observers.get(stock)) {
                // Имитация асинхронности через Thread
                new Thread(() -> observer.update(stock, price)).start();
            }
        }
    }
}

// 4. Конкретные наблюдатели
class Trader implements modul06DZ.IObserver {
    private String name;
    public Trader(String name) { this.name = name; }

    public void update(String stock, double price) {
        System.out.println("[Трейдер " + name + "] Цена " + stock + " изменилась: " + price);
    }
}

class TradingRobot implements modul06DZ.IObserver {
    private double threshold;
    public TradingRobot(double threshold) { this.threshold = threshold; }

    public void update(String stock, double price) {
        if (price < threshold) {
            System.out.println("[РОБОТ] Цена " + stock + " упала ниже " + threshold + ". ПОКУПАЮ!");
        }
    }
}

// ======================================================
// ГЛАВНЫЙ КЛАСС ДЛЯ ЗАПУСКА
// ======================================================
public class Module06 {
    public static void main(String[] args) throws InterruptedException {

        // --- ТЕСТ STRATEGY ---
        System.out.println("=== ТЕСТ ПАТТЕРНА STRATEGY ===");
        TravelBookingContext travelContext = new TravelBookingContext();
        TravelDetails myTrip = new TravelDetails(1000, 2, true, 0.1); // 1000км, 2 чел, бизнес, скидка 10%

        travelContext.setStrategy(new AirplaneStrategy());
        System.out.print("Самолет: "); travelContext.calculateTrip(myTrip);

        travelContext.setStrategy(new TrainStrategy());
        System.out.print("Поезд: "); travelContext.calculateTrip(myTrip);


        // --- ТЕСТ OBSERVER ---
        System.out.println("\n=== ТЕСТ ПАТТЕРНА OBSERVER ===");
        StockExchange exchange = new StockExchange();

        Trader ivan = new Trader("Иван");
        TradingRobot bot = new TradingRobot(150.0);

        exchange.registerObserver("AAPL", ivan); // Иван следит за Apple
        exchange.registerObserver("AAPL", bot);  // Робот тоже следит за Apple
        exchange.registerObserver("TSLA", ivan); // Иван еще следит за Tesla

        System.out.println("\n--- Начало торгов ---");
        exchange.notifyObservers("AAPL", 145.0); // Робот должен купить
        exchange.notifyObservers("TSLA", 700.0); // Только Иван получит уведомление

        Thread.sleep(1000); // Подождем асинхронные уведомления
    }
}