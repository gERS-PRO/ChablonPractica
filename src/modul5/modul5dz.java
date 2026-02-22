package modul5;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;


public class modul5dz {
    public static void main(String[] args) {
        System.out.println("=== Тестирование Singleton в многопоточной среде ===");

        // Создаем пул потоков для имитации одновременного доступа
        ExecutorService executor = Executors.newFixedThreadPool(5);

        Runnable task = () -> {
            ConfigurationManager config = ConfigurationManager.getInstance();
            System.out.println("Поток [" + Thread.currentThread().getName() + "] получил экземпляр: " + config.hashCode());
        };

        // Запускаем несколько потоков одновременно
        for (int i = 0; i < 5; i++) {
            executor.execute(task);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n=== Тестирование функционала настроек ===");
        ConfigurationManager config = ConfigurationManager.getInstance();

        try {
            // 1. Установка и чтение настроек
            config.setSetting("appName", "MyDesignApp");
            config.setSetting("version", "1.0.5");
            System.out.println("AppName: " + config.getSetting("appName"));

            // 2. Сохранение в файл
            config.saveToFile("settings.properties");

            // 3. Загрузка из имитации БД
            config.loadFromDatabase("jdbc:mysql://localhost:3306/configs");

            // 4. Тест обработки исключения (несуществующий ключ)
            System.out.println("Попытка получить несуществующий ключ...");
            config.getSetting("unknown_key");

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}

// Класс ConfigurationManager, реализующий паттерн Одиночка
class ConfigurationManager {
    // volatile гарантирует, что изменения переменной видны всем потокам сразу
    private static volatile ConfigurationManager instance;

    // Словарь для хранения настроек
    private Map<String, String> settings;

    // 1. Приватный конструктор (защита от создания через new)
    private ConfigurationManager() {
        settings = new ConcurrentHashMap<>(); // Потокобезопасная карта
        System.out.println("[System] Создан новый экземпляр ConfigurationManager.");
    }

    // 1. & 3. Статический метод получения экземпляра (Double-Checked Locking)
    public static ConfigurationManager getInstance() {
        ConfigurationManager localInstance = instance;
        if (localInstance == null) {
            synchronized (ConfigurationManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ConfigurationManager();
                }
            }
        }
        return localInstance;
    }

    // 2. Методы для работы с настройками
    public void setSetting(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Ключ и значение не могут быть null.");
        }
        settings.put(key, value);
    }

    public String getSetting(String key) {
        // Доп. задача 2: Обработка исключений
        if (!settings.containsKey(key)) {
            throw new NoSuchElementException("Настройка с ключом '" + key + "' не найдена.");
        }
        return settings.get(key);
    }

    // Задача 3: Сохранение настроек в файл
    public void saveToFile(String filename) {
        Properties props = new Properties();
        props.putAll(settings);
        try (FileOutputStream out = new FileOutputStream(filename)) {
            props.store(out, "Application Settings");
            System.out.println("[File] Настройки сохранены в файл: " + filename);
        } catch (IOException e) {
            System.err.println("[Error] Не удалось сохранить файл: " + e.getMessage());
        }
    }

    // Задача 3: Загрузка настроек из файла
    public void loadFromFile(String filename) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(filename)) {
            props.load(in);
            for (String name : props.stringPropertyNames()) {
                settings.put(name, props.getProperty(name));
            }
            System.out.println("[File] Настройки загружены из файла.");
        } catch (IOException e) {
            System.err.println("[Error] Не удалось загрузить файл: " + e.getMessage());
        }
    }

    // Доп. задача 1: Загрузка из внешних источников (имитация БД)
    public void loadFromDatabase(String dbUrl) {
        System.out.println("[DB] Подключение к базе данных " + dbUrl + "...");
        // Имитация получения данных
        settings.put("db_connection_timeout", "30s");
        settings.put("max_users", "100");
        System.out.println("[DB] Данные из БД успешно загружены.");
    }
}
