package modul5;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

// Уровни логирования
enum LogLevel {
    INFO(1), WARNING(2), ERROR(3);
    final int priority;
    LogLevel(int priority) { this.priority = priority; }
}

// Главный класс программы
public class modul5pr {
    public static void main(String[] args) {
        System.out.println("=== Запуск системы логирования (Singleton) ===\n");

        // 1. Получаем экземпляр логгера
        Logger logger = Logger.getInstance();

        // 3. Имитируем загрузку конфигурации
        // В реальности здесь был бы вызов logger.loadConfig("config.txt");
        logger.setConfig(LogLevel.INFO, "app_logs.txt", true);

        // 4 & 6. Тестирование многопоточности
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 1; i <= 6; i++) {
            final int threadId = i;
            executor.execute(() -> {
                Logger logRef = Logger.getInstance(); // Всегда один и тот же объект
                logRef.log("Поток-" + threadId + " начал работу", LogLevel.INFO);

                if (threadId % 2 == 0) {
                    logRef.log("Поток-" + threadId + " обнаружил проблему!", LogLevel.WARNING);
                }
                if (threadId % 3 == 0) {
                    logRef.log("Поток-" + threadId + " упал с ошибкой!", LogLevel.ERROR);
                }
            });
        }

        executor.shutdown();
        try {
            if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("\n=== Все потоки завершили запись ===\n");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 5. Чтение логов через LogReader
        LogReader reader = new LogReader("app_logs.txt");

        System.out.println("--- Фильтр: Только ОШИБКИ ---");
        reader.readAndDisplay(LogLevel.ERROR);

        System.out.println("\n--- Фильтр: По времени (последние 5 минут) ---");
        reader.readRecent(5);
    }
}

// 1. Класс Logger (Singleton)
class Logger {
    // volatile важен для корректной работы Double-Checked Locking
    private static volatile Logger instance;

    private LogLevel currentLevel = LogLevel.INFO;
    private String logFilePath = "log.txt";
    private boolean logToConsole = true;
    private final long MAX_FILE_SIZE = 1024; // 1 КБ для демонстрации ротации

    // Приватный конструктор
    private Logger() {
        // Инициализация ресурсов
    }

    // Потокобезопасный GetInstance (Double-Checked Locking)
    public static Logger getInstance() {
        Logger localInstance = instance;
        if (localInstance == null) {
            synchronized (Logger.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Logger();
                }
            }
        }
        return localInstance;
    }

    // 3. Установка конфигурации
    public void setConfig(LogLevel level, String path, boolean toConsole) {
        this.currentLevel = level;
        this.logFilePath = path;
        this.logToConsole = toConsole;
    }

    // 2. Основной метод логирования (синхронизирован для записи в файл)
    public synchronized void log(String message, LogLevel level) {
        if (level.priority < currentLevel.priority) return;

        checkRotation(); // Доп. задача: Ротация

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String formattedMessage = String.format("[%s] [%s] %s", timestamp, level, message);

        // Доп. задача 2: Логирование в несколько источников
        if (logToConsole) {
            System.out.println(formattedMessage);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(logFilePath, true))) {
            writer.println(formattedMessage);
        } catch (IOException e) {
            System.err.println("Ошибка записи в лог: " + e.getMessage());
        }
    }

    // Доп. задача 1: Ротация файлов
    private void checkRotation() {
        File file = new File(logFilePath);
        if (file.exists() && file.length() > MAX_FILE_SIZE) {
            String newName = "log_archive_" + System.currentTimeMillis() + ".txt";
            file.renameTo(new File(newName));
            // Новый файл создастся автоматически при следующей записи
        }
    }

    // 4. Загрузка из файла (упрощенная версия для примера)
    public void loadConfig(String configPath) {
        try (Scanner sc = new Scanner(new File(configPath))) {
            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split("=");
                if (parts[0].equals("level")) this.currentLevel = LogLevel.valueOf(parts[1]);
                if (parts[0].equals("path")) this.logFilePath = parts[1];
            }
        } catch (Exception e) {
            System.out.println("Конфиг не найден, используем настройки по умолчанию.");
        }
    }
}

// 5. Класс LogReader
class LogReader {
    private String filePath;

    public LogReader(String filePath) {
        this.filePath = filePath;
    }

    // Фильтрация по уровню
    public void readAndDisplay(LogLevel filter) {
        processFile(line -> line.contains("[" + filter + "]"));
    }

    // Доп. задача 3: Фильтрация по времени (упрощенная)
    public void readRecent(int minutes) {
        LocalDateTime limit = LocalDateTime.now().minusMinutes(minutes);
        processFile(line -> {
            try {
                String datePart = line.substring(1, 20);
                LocalDateTime logTime = LocalDateTime.parse(datePart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                return logTime.isAfter(limit);
            } catch (Exception e) { return false; }
        });
    }

    private void processFile(java.util.function.Predicate<String> filter) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (filter.test(line)) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения логов: " + e.getMessage());
        }
    }
}
