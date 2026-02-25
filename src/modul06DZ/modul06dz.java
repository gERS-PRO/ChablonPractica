package modul06DZ;
import java.util.ArrayList;
import java.util.List;

// ==========================================
// ЧАСТЬ 1: ПАТТЕРН СТРАТЕГИЯ (Оплата)
// ==========================================

// 1. Интерфейс стратегии оплаты
interface IPaymentStrategy {
    void pay(double amount);
}

// 2. Конкретные реализации стратегий
class CreditCardPayment implements IPaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Оплата $" + amount + " произведена через: Банковскую карту.");
    }
}

class PayPalPayment implements IPaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Оплата $" + amount + " произведена через: PayPal.");
    }
}

class CryptoPayment implements IPaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Оплата $" + amount + " произведена через: Криптовалюту (BTC/ETH).");
    }
}

// 3. Класс контекста
class PaymentContext {
    private IPaymentStrategy strategy;

    // Метод для установки или смены стратегии на лету
    public void setPaymentStrategy(IPaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void executePayment(double amount) {
        if (strategy == null) {
            System.err.println("Ошибка: Способ оплаты не выбран!");
        } else {
            strategy.pay(amount);
        }
    }
}

// ==========================================
// ЧАСТЬ 2: ПАТТЕРН НАБЛЮДАТЕЛЬ (Курсы валют)
// ==========================================

// 1. Интерфейс наблюдателя
interface IObserver {
    void update(String currency, double rate);
}

// 2. Интерфейс субъекта
interface ISubject {
    void attach(IObserver observer);
    void detach(IObserver observer);
    void notifyObservers();
}

// 3. Конкретный субъект - Валютная биржа
class CurrencyExchange implements ISubject {
    private List<IObserver> observers = new ArrayList<>();
    private String currencyName;
    private double rate;

    public void setRate(String currencyName, double rate) {
        this.currencyName = currencyName;
        this.rate = rate;
        notifyObservers(); // Уведомляем всех при изменении курса
    }

    @Override
    public void attach(IObserver observer) {
        observers.add(observer);
        System.out.println("Система: Добавлен новый наблюдатель.");
    }

    @Override
    public void detach(IObserver observer) {
        observers.remove(observer);
        System.out.println("Система: Наблюдатель удален.");
    }

    @Override
    public void notifyObservers() {
        for (IObserver observer : observers) {
            observer.update(currencyName, rate);
        }
    }
}

// 4. Конкретные наблюдатели
class NewsAgency implements IObserver {
    @Override
    public void update(String currency, double rate) {
        System.out.println("[НОВОСТИ]: Срочный выпуск! Курс " + currency + " теперь составляет " + rate);
    }
}

class BankSystem implements IObserver {
    @Override
    public void update(String currency, double rate) {
        System.out.println("[БАНК]: Пересчет внутренних курсов обмена для " + currency + " по ставке " + rate);
    }
}

class MobileAppUser implements IObserver {
    private String userName;
    public MobileAppUser(String name) { this.userName = name; }

    @Override
    public void update(String currency, double rate) {
        System.out.println("[ПРИЛОЖЕНИЕ]: Уведомление для " + userName + ": " + currency + " изменился до " + rate);
    }
}

// ==========================================
// ГЛАВНЫЙ КЛАСС (Клиент)
// ==========================================
public class modul06dz {
    public static void main(String[] args) {

        // --- ТЕСТ ПАТТЕРНА СТРАТЕГИЯ ---
        System.out.println("=== ТЕСТ ПАТТЕРНА СТРАТЕГИЯ ===");
        PaymentContext shop = new PaymentContext();

        // Оплата картой
        shop.setPaymentStrategy(new CreditCardPayment());
        shop.executePayment(150.0);

        // Смена стратегии на PayPal
        shop.setPaymentStrategy(new PayPalPayment());
        shop.executePayment(45.99);

        // Оплата криптовалютой
        shop.setPaymentStrategy(new CryptoPayment());
        shop.executePayment(1200.0);


        System.out.println("\n-----------------------------------\n");


        // --- ТЕСТ ПАТТЕРНА НАБЛЮДАТЕЛЬ ---
        System.out.println("=== ТЕСТ ПАТТЕРНА НАБЛЮДАТЕЛЬ ===");
        CurrencyExchange exchange = new CurrencyExchange();

        IObserver news = new NewsAgency();
        IObserver bank = new BankSystem();
        IObserver user = new MobileAppUser("Алексей");

        // Подписываем наблюдателей
        exchange.attach(news);
        exchange.attach(bank);
        exchange.attach(user);

        System.out.println("\n--- Изменение курса USD ---");
        exchange.setRate("USD", 89.5);

        System.out.println("\n--- Изменение курса EUR (один отписался) ---");
        exchange.detach(bank); // Удаляем банк из рассылки
        exchange.setRate("EUR", 97.2);
    }
}