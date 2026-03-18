package modul7;
import java.util.*;

interface Command {
    void execute();
    void undo();
}

class NoCommand implements Command {
    public void execute() {
        System.out.println("Команда не назначена");
    }

    public void undo() {
        System.out.println("Отменять нечего");
    }
}


class Light {
    void on() {
        System.out.println("Свет включен");
    }

    void off() {
        System.out.println("Свет выключен");
    }
}

class AirConditioner {
    void on() {
        System.out.println("Кондиционер включен");
    }

    void off() {
        System.out.println("Кондиционер выключен");
    }
}

class TV {
    void on() {
        System.out.println("Телевизор включен");
    }

    void off() {
        System.out.println("Телевизор выключен");
    }
}


class LightOnCommand implements Command {
    private Light light;

    LightOnCommand(Light light) {
        this.light = light;
    }

    public void execute() {
        light.on();
    }

    public void undo() {
        light.off();
    }
}

class LightOffCommand implements Command {
    private Light light;

    LightOffCommand(Light light) {
        this.light = light;
    }

    public void execute() {
        light.off();
    }

    public void undo() {
        light.on();
    }
}


class ACOnCommand implements Command {
    private AirConditioner ac;

    ACOnCommand(AirConditioner ac) {
        this.ac = ac;
    }

    public void execute() {
        ac.on();
    }

    public void undo() {
        ac.off();
    }
}

class ACOffCommand implements Command {
    private AirConditioner ac;

    ACOffCommand(AirConditioner ac) {
        this.ac = ac;
    }

    public void execute() {
        ac.off();
    }

    public void undo() {
        ac.on();
    }
}

class TVOnCommand implements Command {
    private TV tv;

    TVOnCommand(TV tv) {
        this.tv = tv;
    }

    public void execute() {
        tv.on();
    }

    public void undo() {
        tv.off();
    }
}

class TVOffCommand implements Command {
    private TV tv;

    TVOffCommand(TV tv) {
        this.tv = tv;
    }

    public void execute() {
        tv.off();
    }

    public void undo() {
        tv.on();
    }
}

class MacroCommand implements Command {
    private List<Command> commands;

    MacroCommand(List<Command> commands) {
        this.commands = commands;
    }

    public void execute() {
        for (Command c : commands) {
            c.execute();
        }
    }

    public void undo() {
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }
}

class RemoteControl {
    private Command[] slots;
    private Stack<Command> undoStack = new Stack<>();
    private Stack<Command> redoStack = new Stack<>();

    RemoteControl(int size) {
        slots = new Command[size];

        for (int i = 0; i < size; i++) {
            slots[i] = new NoCommand();
        }
    }

    void setCommand(int slot, Command command) {
        slots[slot] = command;
    }

    void pressButton(int slot) {
        slots[slot].execute();
        undoStack.push(slots[slot]);
        redoStack.clear();
    }

    void undo() {
        if (!undoStack.isEmpty()) {
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
        } else {
            System.out.println("Нет команд для отмены");
        }
    }

    void redo() {
        if (!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
        } else {
            System.out.println("Нет команд для повтора");
        }
    }
}


public class P7E1 {

    public static void main(String[] args) {

        RemoteControl remote = new RemoteControl(5);

        Light light = new Light();
        TV tv = new TV();
        AirConditioner ac = new AirConditioner();

        Command lightOn = new LightOnCommand(light);
        Command tvOn = new TVOnCommand(tv);
        Command acOn = new ACOnCommand(ac);


        remote.setCommand(0, lightOn);
        remote.setCommand(1, tvOn);
        remote.setCommand(2, acOn);

        remote.pressButton(0);
        remote.pressButton(1);

        remote.undo();
        remote.redo();

        System.out.println("\nМакрокоманда:");

        List<Command> party = new ArrayList<>();
        party.add(lightOn);
        party.add(tvOn);
        party.add(acOn);

        Command partyMode = new MacroCommand(party);

        remote.setCommand(3, partyMode);

        remote.pressButton(3);
    }
}
