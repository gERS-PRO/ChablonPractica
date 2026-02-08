package modul3.prac;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(String name, int quantity, double price) {
        items.add(new OrderItem(name, quantity, price));
    }

    public List<OrderItem> getItems() {
        return items;
    }
}

class OrderItem {
    String name;
    int quantity;
    double price;

    public OrderItem(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public double getTotal() {
        return quantity * price;
    }
}
