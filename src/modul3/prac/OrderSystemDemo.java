package modul3.prac;

public class OrderSystemDemo {
    public static void main(String[] args) {

        // Create order
        Order order = new Order();
        order.addItem("Laptop", 1, 500000);
        order.addItem("Mouse", 2, 5000);

        // Calculate total
        double total = 0;
        for (OrderItem item : order.getItems()) {
            total += item.getTotal();
        }

        // Discount
        DiscountCalculator discount = new DiscountCalculator(new TenPercentDiscount());
        total = discount.calculate(total);
        System.out.println("Total with discount: " + total);

        // Payment (DIP)
        IPayment payment = new CreditCardPayment();
        payment.processPayment(total);

        // Delivery (LSP)
        IDelivery delivery = new CourierDelivery();
        delivery.deliverOrder(order);

        // Notification (DIP)
        INotification notification = new EmailNotification();
        notification.sendNotification("Order completed!");
    }
}

