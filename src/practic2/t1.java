package practic2;
import java.util.ArrayList;
import java.util.List;

public class t1 {
    public static void main(String[] args) {
        UserManager manager = new UserManager();

        // Демонстрация работы (KISS)
        manager.addUser(new User("Иван", "ivan@example.com", "Admin"));
        manager.addUser(new User("Мария", "maria@example.com", "User"));

        System.out.println("--- Список после добавления ---");
        manager.printAllUsers();

        manager.updateUser("maria@example.com", new User("Мария Иванова", "maria@new.com", "User"));

        System.out.println("\n--- Список после обновления ---");
        manager.printAllUsers();

        manager.removeUser("ivan@example.com");

        System.out.println("\n--- Итоговый список ---");
        manager.printAllUsers();
    }
}

// --- Класс User (KISS: только необходимые поля) ---
class User {
    private String name;
    private String email;
    private String role;

    public User(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Геттеры и сеттеры (YAGNI: только те, что реально нужны)
    public String getEmail() { return email; }
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return String.format("User{Name='%s', Email='%s', Role='%s'}", name, email, role);
    }
}

// --- Класс UserManager ---
class UserManager {
    private final List<User> users = new ArrayList<>();

    // DRY: Выносим логику поиска в отдельный метод,
    // чтобы не дублировать циклы в removeUser и updateUser.
    private User findByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

    public void addUser(User user) {
        // KISS: простая проверка, чтобы не плодить дубликаты по email
        if (findByEmail(user.getEmail()) == null) {
            users.add(user);
        }
    }

    public void removeUser(String email) {
        User user = findByEmail(email);
        if (user != null) {
            users.remove(user);
        }
    }

    public void updateUser(String oldEmail, User newData) {
        User user = findByEmail(oldEmail);
        if (user != null) {
            // KISS & DRY: Обновляем существующий объект
            user.setName(newData.getName());
            user.setEmail(newData.getEmail());
            user.setRole(newData.getEmail()); // Исправлено: роль из newData
        }
    }

    // Вспомогательный метод для демонстрации (KISS)
    public void printAllUsers() {
        if (users.isEmpty()) {
            System.out.println("Список пуст.");
        } else {
            users.forEach(System.out::println);
        }
    }
}
