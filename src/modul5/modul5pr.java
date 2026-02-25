package modul5;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// ==========================================
// 1. ПАТТЕРН SINGLETON + ЛОГИРОВАНИЕ
// ==========================================
enum LogLevel { INFO, WARNING, ERROR }

class Logger {
    private static volatile Logger instance;
    private LogLevel currentLevel = LogLevel.INFO;
    private String logFilePath = "app_logs.txt";

    private Logger() {}

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) instance = new Logger();
            }
        }
        return instance;
    }

    public void setConfig(LogLevel level, String path) {
        this.currentLevel = level;
        this.logFilePath = path;
    }

    public synchronized void log(String message, LogLevel level) {
        // Проверка уровня: пишем только если уровень сообщения >= текущего
        if (level.ordinal() < currentLevel.ordinal()) return;

        String logEntry = "[" + level + "] " + message;
        System.out.println(logEntry); // В консоль

        try (PrintWriter out = new PrintWriter(new FileWriter(logFilePath, true))) {
            out.println(logEntry); // В файл
        } catch (IOException e) { e.printStackTrace(); }
    }
}

// ==========================================
// 2. ПАТТЕРН BUILDER (Отчеты со стилями)
// ==========================================
class ReportStyle {
    String bgColor;
    int fontSize;
    public ReportStyle(String bg, int size) { this.bgColor = bg; this.fontSize = size; }
}

class Report {
    String header, content, footer;
    List<String> sections = new ArrayList<>();
    ReportStyle style;

    public void export() {
        System.out.println("--- Exporting Report ---");
        System.out.println("Style: [BG: " + style.bgColor + ", Size: " + style.fontSize + "]");
        System.out.println("Header: " + header);
        for(String s : sections) System.out.println("Section: " + s);
        System.out.println("Footer: " + footer);
    }
}

interface IReportBuilder {
    void setHeader(String h);
    void addSection(String name, String content);
    void setFooter(String f);
    void setStyle(ReportStyle style);
    Report getReport();
}

class HtmlReportBuilder implements IReportBuilder {
    private Report report = new Report();
    public void setHeader(String h) { report.header = "<h1>" + h + "</h1>"; }
    public void addSection(String n, String c) { report.sections.add("<div>" + n + ": " + c + "</div>"); }
    public void setFooter(String f) { report.footer = "<footer>" + f + "</footer>"; }
    public void setStyle(ReportStyle s) { report.style = s; }
    public Report getReport() { return report; }
}

class ReportDirector {
    public void construct(IReportBuilder builder) {
        builder.setHeader("Standard Report");
        builder.addSection("Introduction", "This is the content.");
        builder.setFooter("Page 1");
        builder.setStyle(new ReportStyle("White", 12));
    }
}

// ==========================================
// 3. ПАТТЕРН PROTOTYPE (Глубокое копирование)
// ==========================================
class Weapon implements Cloneable {
    String name;
    public Weapon(String n) { this.name = n; }
    @Override public Weapon clone() throws CloneNotSupportedException { return (Weapon) super.clone(); }
}

class Character implements Cloneable {
    String name;
    Weapon weapon;
    List<String> skills = new ArrayList<>();

    public Character(String n, Weapon w) { this.name = n; this.weapon = w; }

    @Override
    public Character clone() throws CloneNotSupportedException {
        Character copy = (Character) super.clone();
        copy.weapon = this.weapon.clone(); // Глубокое копирование объекта
        copy.skills = new ArrayList<>(this.skills); // Глубокое копирование списка
        return copy;
    }

    public void info() {
        System.out.println("Hero: " + name + ", Weapon: " + weapon.name + ", Skills: " + skills);
    }
}

// ==========================================
// ГЛАВНЫЙ КЛАСС (Тестирование)
// ==========================================
public class modul5pr {
    public static void main(String[] args) throws Exception {
        // 1. Тест Singleton (Многопоточность)
        System.out.println("=== SINGLETON TEST ===");
        Logger logger = Logger.getInstance();
        ExecutorService exec = Executors.newFixedThreadPool(2);
        exec.execute(() -> logger.log("Thread 1 writing...", LogLevel.INFO));
        exec.execute(() -> logger.log("Thread 2 writing...", LogLevel.ERROR));
        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.SECONDS);

        // 2. Тест Builder
        System.out.println("\n=== BUILDER TEST ===");
        IReportBuilder htmlBuilder = new HtmlReportBuilder();
        ReportDirector director = new ReportDirector();
        director.construct(htmlBuilder);
        htmlBuilder.getReport().export();

        // 3. Тест Prototype
        System.out.println("\n=== PROTOTYPE TEST ===");
        Character original = new Character("Warrior", new Weapon("Axe"));
        original.skills.add("Strike");

        Character clone = original.clone();
        clone.name = "Clone";
        clone.weapon.name = "Dagger"; // Не должно изменить оригинал
        clone.skills.add("Hide");    // Не должно изменить оригинал

        System.out.print("Original: "); original.info();
        System.out.print("Clone: "); clone.info();
    }
}