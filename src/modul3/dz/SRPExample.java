package modul3.dz;
public class SRPExample {
    public static void main(String[] args) {
        Order order = new Order("Phone", 2, 500);

        PriceCalculator calculator = new DiscountCalculator();
        System.out.println("Total price: " + calculator.calculate(order));

        PaymentProcessor payment = new CardPayment();
        payment.process("VISA");

        Notifier notifier = new EmailNotifier();
        notifier.send("test@mail.com", "Order confirmed");
    }
}

// Тек тапсырыс деректері
class Order {
    String name;
    int quantity;
    double price;

    public Order(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}

// Баға есептеу
interface PriceCalculator {
    double calculate(Order order);
}

class DiscountCalculator implements PriceCalculator {
    public double calculate(Order order) {
        return order.quantity * order.price * 0.9;
    }
}

// Төлем өңдеу
interface PaymentProcessor {
    void process(String details);
}

class CardPayment implements PaymentProcessor {
    public void process(String details) {
        System.out.println("Payment processed: " + details);
    }
}

// Хабарлама жіберу
interface Notifier {
    void send(String email, String message);
}

class EmailNotifier implements Notifier {
    public void send(String email, String message) {
        System.out.println("Email sent to " + email);
    }
}
