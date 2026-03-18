import java.util.*;

// ==========================================
// 1. ПАТТЕРН КОМАНДА (COMMAND)
// ==========================================



interface ICommand {
    void execute();
    void undo();
}

// "Null Object" паттерні - қателерді болдырмау үшін
class NoCommand implements ICommand {
    public void execute() { System.out.println("Команда тағайындалмаған"); }
    public void undo() { System.out.println("Болдырмайтын ештеңе жоқ"); }
}

// Receivers - Құрылғылар
class Light {
    public void on() { System.out.println("Жарық қосылды"); }
    public void off() { System.out.println("Жарық өшірілді"); }
}

class Thermostat {
    private int temperature = 20;
    public void setTemperature(int temp) {
        this.temperature = temp;
        System.out.println("Температура " + temperature + "°C-қа орнатылды");
    }
    public int getTemperature() { return temperature; }
}

class TV {
    public void on() { System.out.println("Теледидар қосылды"); }
    public void off() { System.out.println("Теледидар өшірілді"); }
}

// Нақты командалар
class LightCommand implements ICommand {
    private Light light;
    public LightCommand(Light light) { this.light = light; }
    @Override public void execute() { light.on(); }
    @Override public void undo() { light.off(); }
}

class TVCommand implements ICommand {
    private TV tv;
    public TVCommand(TV tv) { this.tv = tv; }
    @Override public void execute() { tv.on(); }
    @Override public void undo() { tv.off(); }
}

class TempCommand implements ICommand {
    private Thermostat thermostat;
    private int prevTemp;
    private int newTemp;
    public TempCommand(Thermostat thermostat, int newTemp) {
        this.thermostat = thermostat;
        this.newTemp = newTemp;
    }
    @Override
    public void execute() {
        prevTemp = thermostat.getTemperature();
        thermostat.setTemperature(newTemp);
    }
    @Override
    public void undo() {
        thermostat.setTemperature(prevTemp);
    }
}

class MacroCommand implements ICommand {
    private List<ICommand> commands;
    public MacroCommand(List<ICommand> commands) { this.commands = commands; }
    @Override
    public void execute() {
        System.out.println("--- Макрокоманда іске қосылды ---");
        for (ICommand c : commands) c.execute();
    }
    @Override
    public void undo() {
        System.out.println("--- Макрокоманданы болдырмау ---");
        for (int i = commands.size() - 1; i >= 0; i--) commands.get(i).undo();
    }
}

// Invoker - Пульт (Undo және Redo қолдауымен)
class RemoteControl {
    private ICommand[] slots;
    private Stack<ICommand> undoStack = new Stack<>();
    private Stack<ICommand> redoStack = new Stack<>();

    public RemoteControl(int size) {
        slots = new ICommand[size];
        for (int i = 0; i < size; i++) slots[i] = new NoCommand();
    }

    public void setCommand(int slot, ICommand command) {
        slots[slot] = command;
    }

    public void pressButton(int slot) {
        slots[slot].execute();
        undoStack.push(slots[slot]);
        redoStack.clear(); // Жаңа команда жасалғанда Redo тарихы өшеді
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            ICommand cmd = undoStack.pop();
            System.out.print("[UNDO]: ");
            cmd.undo();
            redoStack.push(cmd);
        } else {
            System.out.println("Болдырмайтын команда жоқ!");
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            ICommand cmd = redoStack.pop();
            System.out.print("[REDO]: ");
            cmd.execute();
            undoStack.push(cmd);
        } else {
            System.out.println("Қайталайтын команда жоқ!");
        }
    }
}

// ==========================================
// 2. ПАТТЕРН ШАБЛОННЫЙ МЕТОД (TEMPLATE METHOD)
// ==========================================



abstract class Beverage {
    public final void prepareRecipe() {
        boilWater();
        brew();
        pourInCup();
        if (customerWantsCondiments()) addCondiments();
    }
    private void boilWater() { System.out.println("Су қайнап жатыр..."); }
    private void pourInCup() { System.out.println("Шыны аяққа құйылуда..."); }
    protected abstract void brew();
    protected abstract void addCondiments();
    protected boolean customerWantsCondiments() { return true; } // Hook
}

class Coffee extends Beverage {
    @Override protected void brew() { System.out.println("Кофе демделуде..."); }
    @Override protected void addCondiments() { System.out.println("Қант пен сүт қосылды."); }
}

// ==========================================
// 3. ПАТТЕРН ПОСРЕДНИК (MEDIATOR)
// ==========================================



interface IMediator {
    void sendMessage(String msg, User user);
    void addUser(User user);
}

class ChatRoom implements IMediator {
    private List<User> users = new ArrayList<>();
    @Override public void addUser(User user) { users.add(user); }
    @Override
    public void sendMessage(String msg, User sender) {
        for (User u : users) {
            if (u != sender) u.receive(msg, sender.name);
        }
    }
}

abstract class User {
    protected IMediator mediator;
    protected String name;
    public User(IMediator m, String n) { this.mediator = m; this.name = n; }
    public abstract void send(String msg);
    public abstract void receive(String msg, String from);
}

class ChatUser extends User {
    public ChatUser(IMediator m, String n) { super(m, n); }
    @Override public void send(String msg) { mediator.sendMessage(msg, this); }
    @Override public void receive(String msg, String from) {
        System.out.println(name + " (" + from + " арқылы): " + msg);
    }
}

// ==========================================
// КЛИЕНТТІК КОД
// ==========================================

public class DesignPatternsModule07 {
    public static void main(String[] args) {
        // 1. Команда паттерні тесті
        System.out.println("--- КОМАНДА ТЕСТІ (UNDO/REDO/MACRO) ---");
        RemoteControl remote = new RemoteControl(5);
        Light light = new Light();
        Thermostat term = new Thermostat();

        ICommand lightOn = new LightCommand(light);
        ICommand tempSet = new TempCommand(term, 25);

        // Макрокоманда жасау (Кешкі режим: жарық + жылу)
        MacroCommand eveningMode = new MacroCommand(Arrays.asList(lightOn, tempSet));

        remote.setCommand(0, lightOn);
        remote.setCommand(1, eveningMode);

        remote.pressButton(0); // Жарық қосу
        remote.undo();         // Болдырмау
        remote.redo();         // Қайталау

        System.out.println("\n--- Макро тесті ---");
        remote.pressButton(1); // Макро (Жарық + Темп)
        remote.undo();         // Екеуін де болдырмау

        // 2. Шаблонный метод тесті
        System.out.println("\n--- ШАБЛОНДЫҚ МЕТОД ТЕСТІ ---");
        Beverage coffee = new Coffee();
        coffee.prepareRecipe();

        // 3. Посредник тесті
        System.out.println("\n--- ПОСРЕДНИК ТЕСТІ ---");
        IMediator chat = new ChatRoom();
        User u1 = new ChatUser(chat, "Әли");
        User u2 = new ChatUser(chat, "Дина");
        chat.addUser(u1);
        chat.addUser(u2);
        u1.send("Сәлем, достар!");
    }
}