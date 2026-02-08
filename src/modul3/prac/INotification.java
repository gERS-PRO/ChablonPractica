package modul3.prac;

public interface INotification {
    void sendNotification(String message);
}

class EmailNotification implements INotification {
    public void sendNotification(String message) {
        System.out.println("Email: " + message);
    }
}

class SmsNotification implements INotification {
    public void sendNotification(String message) {
        System.out.println("SMS: " + message);
    }
}
