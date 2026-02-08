package modul3.dz;
public class DIPExample {
    public static void main(String[] args) {
        MessageSender sender = new EmailSender();
        NotificationService service = new NotificationService(sender);
        service.send("Hello world");
    }
}

// Абстракция
interface MessageSender {
    void send(String message);
}

// Нақты реализациялар
class EmailSender implements MessageSender {
    public void send(String message) {
        System.out.println("Email: " + message);
    }
}

class SmsSender implements MessageSender {
    public void send(String message) {
        System.out.println("SMS: " + message);
    }
}

// Жоғары деңгейлі класс
class NotificationService {
    private MessageSender sender;

    public NotificationService(MessageSender sender) {
        this.sender = sender;
    }

    public void send(String message) {
        sender.send(message);
    }
}
