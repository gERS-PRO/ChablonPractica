package modul8;
import java.util.ArrayList;
import java.util.List;

// ==========================================
// ЧАСТЬ 1: ПАТТЕРН ДЕКОРАТОР (Кафе)
// ==========================================

// 1. Базовый абстрактный класс напитка
abstract class Beverage {
    String description = "Неизвестный напиток";

    public String getDescription() {
        return description;
    }

    public abstract double getCost();
}

// 2. Конкретные напитки
class Espresso extends Beverage {
    public Espresso() { description = "Эспрессо"; }
    @Override public double getCost() { return 50.0; }
}

class Tea extends Beverage {
    public Tea() { description = "Чай"; }
    @Override public double getCost() { return 30.0; }
}

class Latte extends Beverage {
    public Latte() { description = "Латте"; }
    @Override public double getCost() { return 70.0; }
}

class Mocha extends Beverage {
    public Mocha() { description = "Мокка"; }
    @Override public double getCost() { return 80.0; }
}

// 3. Абстрактный декоратор
abstract class BeverageDecorator extends Beverage {
    protected Beverage beverage; // Ссылка на декорируемый объект

    public BeverageDecorator(Beverage beverage) {
        this.beverage = beverage;
    }

    public abstract String getDescription();
}

// 4. Конкретные декораторы (Добавки)
class Milk extends BeverageDecorator {
    public Milk(Beverage beverage) { super(beverage); }

    @Override public String getDescription() {
        return beverage.getDescription() + ", Молоко";
    }

    @Override public double getCost() {
        return beverage.getCost() + 15.0; // Цена молока
    }
}

class Sugar extends BeverageDecorator {
    public Sugar(Beverage beverage) { super(beverage); }

    @Override public String getDescription() {
        return beverage.getDescription() + ", Сахар";
    }

    @Override public double getCost() {
        return beverage.getCost() + 5.0;
    }
}

class WhippedCream extends BeverageDecorator {
    public WhippedCream(Beverage beverage) { super(beverage); }

    @Override public String getDescription() {
        return beverage.getDescription() + ", Взбитые сливки";
    }

    @Override public double getCost() {
        return beverage.getCost() + 25.0;
    }
}

class Syrup extends BeverageDecorator {
    public Syrup(Beverage beverage) { super(beverage); }

    @Override public String getDescription() {
        return beverage.getDescription() + ", Сироп";
    }

    @Override public double getCost() {
        return beverage.getCost() + 20.0;
    }
}

// ==========================================
// ЧАСТЬ 2: ПАТТЕРН АДАПТЕР (Платежи)
// ==========================================

// 1. Наш целевой интерфейс
interface IPaymentProcessor {
    void processPayment(double amount);
}

// 2. Наша текущая реализация
class PayPalPaymentProcessor implements IPaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Оплата $" + amount + " успешно проведена через PayPal.");
    }
}

// 3. Сторонние сервисы с ДРУГИМИ интерфейсами
class StripePaymentService {
    public void makeTransaction(double totalAmount) {
        System.out.println("Транзакция Stripe на сумму $" + totalAmount + " завершена.");
    }
}

class SquarePaymentService {
    // Допустим, Square принимает сумму в центах (целое число)
    public void completePayment(int cents) {
        System.out.println("Платеж Square на сумму " + cents + " центов обработан.");
    }
}

// 4. Адаптеры
class StripePaymentAdapter implements IPaymentProcessor {
    private StripePaymentService stripeService;

    public StripePaymentAdapter(StripePaymentService service) {
        this.stripeService = service;
    }

    @Override
    public void processPayment(double amount) {
        // Перенаправляем вызов в метод стороннего сервиса
        stripeService.makeTransaction(amount);
    }
}

class SquarePaymentAdapter implements IPaymentProcessor {
    private SquarePaymentService squareService;

    public SquarePaymentAdapter(SquarePaymentService service) {
        this.squareService = service;
    }

    @Override
    public void processPayment(double amount) {
        // Конвертируем доллары в центы перед отправкой
        int cents = (int) (amount * 100);
        squareService.completePayment(cents);
    }
}

// ==========================================
// КЛИЕНТСКИЙ КОД (Main)
// ==========================================
public class M8DZ {
    public static void main(String[] args) {

        // --- ТЕСТ ДЕКОРАТОРА ---
        System.out.println("=== ЗАКАЗЫ В КАФЕ (Decorator) ===");

        // Заказываем сложный кофе: Мокка + Молоко + Сироп + Сливки
        Beverage myOrder = new Mocha();
        myOrder = new Milk(myOrder);
        myOrder = new Syrup(myOrder);
        myOrder = new WhippedCream(myOrder);

        System.out.println("Заказ: " + myOrder.getDescription());
        System.out.println("Итоговая стоимость: " + myOrder.getCost() + " руб.");

        // Заказываем простой чай с сахаром
        Beverage simpleTea = new Tea();
        simpleTea = new Sugar(simpleTea);
        System.out.println("\nЗаказ: " + simpleTea.getDescription());
        System.out.println("Итоговая стоимость: " + simpleTea.getCost() + " руб.");


        System.out.println("\n-----------------------------------\n");


        // --- ТЕСТ АДАПТЕРА ---
        System.out.println("=== ПЛАТЕЖНЫЕ СИСТЕМЫ (Adapter) ===");

        List<IPaymentProcessor> processors = new ArrayList<>();

        // Добавляем родной PayPal
        processors.add(new PayPalPaymentProcessor());

        // Добавляем Stripe через адаптер
        processors.add(new StripePaymentAdapter(new StripePaymentService()));

        // Добавляем Square через адаптер
        processors.add(new SquarePaymentAdapter(new SquarePaymentService()));

        double billAmount = 99.50;

        for (IPaymentProcessor processor : processors) {
            processor.processPayment(billAmount);
        }
    }
}
