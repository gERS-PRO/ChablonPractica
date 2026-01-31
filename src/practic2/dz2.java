package practic2;
import java.util.Arrays;

/**
 * Тема: Базовые принципы проектирования (DRY, KISS, YAGNI)
 */
public class dz2 {

    public static void main(String[] args) {
        // Здесь можно протестировать работу методов, если потребуется
        System.out.println("Домашнее задание готово к проверке.");
    }

    // ==========================================================
    // 1. ПРИНЦИП DRY (Don't Repeat Yourself)
    // ==========================================================

    // Использование параметризованных методов
    // DRY: Объединяем три метода в один универсальный
    public void log(String message, String level) {
        System.out.println(level.toUpperCase() + ": " + message);
    }

    // Использование общих конфигурационных настроек
    static class Config {
        public static final String CONNECTION_STRING = "Server=myServer;Database=myDb;User Id=myUser;Password=myPass;";
    }

    class DatabaseService {
        public void connect() {
            String dbUrl = Config.CONNECTION_STRING;
            // Логика подключения
        }
    }

    class LoggingService {
        public void logToDb(String message) {
            String dbUrl = Config.CONNECTION_STRING;
            // Логика записи лога
        }
    }

    // ==========================================================
    // 2. ПРИНЦИП KISS (Keep It Simple, Stupid)
    // ==========================================================

    // KISS: Избегаем вложенности с помощью Guard Clauses (защитных условий)
    public void processNumbers(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            return;
        }

        for (int number : numbers) {
            if (number > 0) {
                System.out.println(number);
            }
        }
    }

    // KISS: Избегаем сложного Stream API (LINQ в Java), если нужен просто вывод
    public void printPositiveNumbers(int[] numbers) {
        if (numbers == null) return;

        for (int n : numbers) {
            if (n > 0) {
                System.out.println(n);
            }
        }
    }

    // KISS: Избегаем исключений там, где достаточно простой проверки
    public int divide(int a, int b) {
        return (b == 0) ? 0 : a / b;
    }

    // ==========================================================
    // 3. ПРИНЦИП YAGNI (You Ain't Gonna Need It)
    // ==========================================================

    // YAGNI: Оставляем только данные. Сохранение и отправка писем — это другие задачи.
    class User {
        public String name;
        public String email;
        public String address;
    }

    // YAGNI: Убираем неиспользуемые параметры буферизации
    class FileReader {
        public String readFile(String filePath) {
            // Реализуем только то, что нужно сейчас
            return "file content";
        }
    }

    // YAGNI: Реализуем только тот формат, который востребован
    class ReportGenerator {
        public void generatePdfReport() {
            // Генерируем только PDF, так как Excel и HTML не заказывали
        }
    }
}
