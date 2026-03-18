package modul7.pr;
import java.util.*;

// ========================================================
// ПАТТЕРН 1: КОМАНДА (COMMAND) — Система "Умный дом"
// ========================================================

// 1. Общий интерфейс для всех команд
interface ICommand {
    void execute();
    void undo();
}

// 2. Класс NoCommand (Null Object) для обработки пустых слотов
class NoCommand implements ICommand {
    public void execute() { System.out.println("Слот не назначен."); }
    public void undo() {}
}

// --- Устройства (Receivers) ---

class Light {
    public void on() { System.out.println("Свет включен"); }
    public void off() { System.out.println("Свет выключен"); }
}

class TV {
    public void on() { System.out.println("Телевизор включен"); }
    public void off() { System.out.println("Телевизор выключен"); }
}

class AirConditioner {
    public void setTemperature(int temp) { System.out.println("Кондиционер: температура " + temp + "°C"); }
    public void off() { System.out.println("Кондиционер выключен"); }
}

class SmartCurtains {
    public void open() { System.out.println("Шторы открыты"); }
    public void close() { System.out.println("Шторы закрыты"); }
}

// --- Конкретные команды ---

class LightOnCommand implements ICommand {
    private Light light;
    public LightOnCommand(Light light) { this.light = light; }
    public void execute() { light.on(); }
    public void undo() { light.off(); }
}

class LightOffCommand implements ICommand {
    private Light light;
    public LightOffCommand(Light light) { this.light = light; }
    public void execute() { light.off(); }
    public void undo() { light.on(); }
}

class TVOnCommand implements ICommand {
    private TV tv;
    public TVOnCommand(TV tv) { this.tv = tv; }
    public void execute() { tv.on(); }
    public void undo() { tv.off(); }
}

class ACOnCommand implements ICommand {
    private AirConditioner ac;
    public ACOnCommand(AirConditioner ac) { this.ac = ac; }
    public void execute() { ac.setTemperature(22); }
    public void undo() { ac.off(); }
}

// 5. Макрокоманда — выполнение группы команд
class MacroCommand implements ICommand {
    private List<ICommand> commands;
    public MacroCommand(List<ICommand> commands) { this.commands = commands; }

    public void execute() {
        System.out.println("--- Выполнение макрокоманды ---");
        for (ICommand cmd : commands) cmd.execute();
    }

    public void undo() {
        System.out.println("--- Отмена макрокоманды ---");
        // Отмена в обратном порядке
        for (int i = commands.size() - 1; i >= 0; i--) commands.get(i).undo();
    }
}

// 4. Класс RemoteControl (Invoker)
class RemoteControl {
    private ICommand[] slots;
    private Stack<ICommand> undoStack = new Stack<>();
    private Stack<ICommand> redoStack = new Stack<>();

    public RemoteControl(int slotCount) {
        slots = new ICommand[slotCount];
        for (int i = 0; i < slotCount; i++) slots[i] = new NoCommand();
    }

    public void setCommand(int slot, ICommand command) {
        slots[slot] = command;
    }

    public void pressButton(int slot) {
        slots[slot].execute();
        if (!(slots[slot] instanceof NoCommand)) {
            undoStack.push(slots[slot]);
            redoStack.clear(); // Новая команда очищает историю повтора
        }
    }

    public void pressUndo() {
        if (!undoStack.isEmpty()) {
            ICommand command = undoStack.pop();
            System.out.print("[ОТМЕНА] ");
            command.undo();
            redoStack.push(command);
        } else {
            System.out.println("Нет действий для отмены.");
        }
    }

    public void pressRedo() {
        if (!redoStack.isEmpty()) {
            ICommand command = redoStack.pop();
            System.out.print("[ПОВТОР] ");
            command.execute();
            undoStack.push(command);
        } else {
            System.out.println("Нет действий для повтора.");
        }
    }
}

// ========================================================
// ПАТТЕРН 2: ШАБЛОННЫЙ МЕТОД (TEMPLATE METHOD) — Отчеты
// ========================================================



abstract class ReportGenerator {
    // Шаблонный метод
    public final void generateReport() {
        logStep("Начало генерации отчета");
        fetchData();
        formatHeader();
        formatBody();
        formatFooter();

        if (customerWantsSave()) {
            saveReport();
        } else {
            sendByEmail();
        }
        logStep("Завершение генерации отчета\n");
    }

    // Общие шаги
    private void fetchData() { System.out.println("Извлечение данных из базы данных..."); }
    private void logStep(String message) { System.out.println("[LOG]: " + message); }

    // Уникальные шаги (абстрактные)
    protected abstract void formatHeader();
    protected abstract void formatBody();
    protected abstract void formatFooter();
    protected abstract void saveReport();

    // Hook (перехватываемый метод) для отправки по почте
    protected void sendByEmail() {
        System.out.println("Отчет отправлен по Email.");
    }

    // Hook с валидацией пользовательского ввода
    protected boolean customerWantsSave() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Вы хотите сохранить отчет локально? (y/n): ");
            String answer = scanner.nextLine().toLowerCase();
            if (answer.equals("y")) return true;
            if (answer.equals("n")) return false;
            System.out.println("Ошибка: Введите 'y' или 'n'.");
        }
    }
}

class PdfReport extends ReportGenerator {
    protected void formatHeader() { System.out.println("PDF: Форматирование заглавной страницы с логотипом."); }
    protected void formatBody() { System.out.println("PDF: Отрисовка текста и таблиц в векторном формате."); }
    protected void formatFooter() { System.out.println("PDF: Добавление водяных знаков и номеров страниц."); }
    protected void saveReport() { System.out.println("PDF: Файл сохранен как 'report.pdf'."); }
}

class ExcelReport extends ReportGenerator {
    protected void formatHeader() { System.out.println("Excel: Создание шапки таблицы (закрепление строк)."); }
    protected void formatBody() { System.out.println("Excel: Заполнение ячеек данными и формулами."); }
    protected void formatFooter() { System.out.println("Excel: Создание итоговых графиков на листе."); }
    protected void saveReport() { System.out.println("Excel: Файл сохранен как 'data.xlsx'."); }
}

class CsvReport extends ReportGenerator {
    protected void formatHeader() { System.out.println("CSV: Запись имен столбцов через запятую."); }
    protected void formatBody() { System.out.println("CSV: Запись строк данных в текстовом формате."); }
    protected void formatFooter() { System.out.println("CSV: Запись контрольной суммы."); }
    protected void saveReport() { System.out.println("CSV: Файл сохранен как 'export.csv'."); }

    // Переопределяем hook, чтобы CSV всегда сохранялся без вопросов
    @Override
    protected boolean customerWantsSave() { return true; }
}

// ========================================================
// КЛИЕНТСКИЙ КОД
// ========================================================

public class Main {
    public static void main(String[] args) {

        System.out.println("=== ТЕСТИРОВАНИЕ ПАТТЕРНА КОМАНДА ===");
        RemoteControl remote = new RemoteControl(5);

        Light light = new Light();
        TV tv = new TV();
        SmartCurtains curtains = new SmartCurtains();

        remote.setCommand(0, new LightOnCommand(light));
        remote.setCommand(1, new TVOnCommand(tv));

        // Создание макрокоманды "Ухожу из дома"
        List<ICommand> leaveHomeCommands = Arrays.asList(
                new LightOffCommand(light),
                new CurtainsOpenCommand(curtains)
        );
        remote.setCommand(2, new MacroCommand(leaveHomeCommands));

        remote.pressButton(0); // Свет
        remote.pressButton(4); // Пустой слот
        remote.pressUndo();    // Отмена света
        remote.pressRedo();    // Повтор включения света

        remote.pressButton(2); // Макрокоманда
        remote.pressUndo();    // Отмена всей группы

        System.out.println("\n=== ТЕСТИРОВАНИЕ ПАТТЕРНА ШАБЛОННЫЙ МЕТОД ===");
        ReportGenerator pdf = new PdfReport();
        pdf.generateReport();

        ReportGenerator csv = new CsvReport();
        csv.generateReport();
    }
}
