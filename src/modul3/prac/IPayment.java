package modul3.prac;
public interface IPayment {
    void processPayment(double amount);
}

// Credit Card
class CreditCardPayment implements IPayment {
    public void processPayment(double amount) {
        System.out.println("Paid by Credit Card: " + amount);
    }
}

// PayPal
class PayPalPayment implements IPayment {
    public void processPayment(double amount) {
        System.out.println("Paid by PayPal: " + amount);
    }
}

// Bank Transfer
class BankTransferPayment implements IPayment {
    public void processPayment(double amount) {
        System.out.println("Paid by Bank Transfer: " + amount);
    }
}
