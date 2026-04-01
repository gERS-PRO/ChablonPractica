package modul9;
import java.util.*;

// ============================================================================
// 1. ПАТТЕРН ФАСАД (FACADE) - Мультимедиа орталығы
// ============================================================================

// --- Кіші жүйелер (Subsystems) ---

class TV {
    public void on() { System.out.println("Теледидар қосылды."); }
    public void off() { System.out.println("Теледидар өшірілді."); }
    public void setChannel(int channel) { System.out.println("Теледидар: " + channel + " арна таңдалды."); }
}

class AudioSystem {
    public void on() { System.out.println("Аудиожүйе қосылды."); }
    public void off() { System.out.println("Аудиожүйе өшірілді."); }
    public void setVolume(int level) { System.out.println("Дыбыс деңгейі: " + level); }
}

class DVDPlayer {
    public void play() { System.out.println("DVD: Бейне ойнатылуда..."); }
    public void pause() { System.out.println("DVD: Пауза."); }
    public void stop() { System.out.println("DVD: Тоқтатылды."); }
}

class GameConsole {
    public void on() { System.out.println("Ойын консолі қосылды."); }
    public void startGame(String gameName) { System.out.println("Ойын іске қосылды: " + gameName); }
}

// --- Фасад (HomeTheaterFacade) ---



class HomeTheaterFacade {
    private TV tv;
    private AudioSystem audio;
    private DVDPlayer dvd;
    private GameConsole console;

    public HomeTheaterFacade(TV tv, AudioSystem audio, DVDPlayer dvd, GameConsole console) {
        this.tv = tv;
        this.audio = audio;
        this.dvd = dvd;
        this.console = console;
    }

    // Фильм көру сценарийі
    public void watchMovie() {
        System.out.println("\n--- Фильм көруге дайындық ---");
        tv.on();
        audio.on();
        audio.setVolume(20);
        dvd.play();
    }

    // Жүйені өшіру
    public void endMovie() {
        System.out.println("\n--- Жүйені өшіру ---");
        dvd.stop();
        audio.off();
        tv.off();
    }

    // Ойын ойнау сценарийі
    public void playGame(String title) {
        System.out.println("\n--- Ойынды іске қосу ---");
        tv.on();
        console.on();
        console.startGame(title);
    }

    // Музыка тыңдау (Қосымша сценарий)
    public void listenToMusic() {
        System.out.println("\n--- Музыка тыңдау режимі ---");
        tv.on();
        tv.setChannel(100); // Музыкалық арна
        audio.on();
        audio.setVolume(15);
    }

    // Дыбысты реттеу фасады
    public void setVolume(int level) {
        audio.setVolume(level);
    }
}

// ============================================================================
// 2. ПАТТЕРН КОМПОНОВЩИК (COMPOSITE) - Файлдық жүйе
// ============================================================================



// Ортақ компонент интерфейсі
abstract class FileSystemComponent {
    protected String name;
    public FileSystemComponent(String name) { this.name = name; }

    public abstract void display(String spacing);
    public abstract int getSize();
}

// Leaf (Жапырақ) - Файл
class File extends FileSystemComponent {
    private int size;

    public File(String name, int size) {
        super(name);
        this.size = size;
    }

    @Override
    public void display(String spacing) {
        System.out.println(spacing + "- Файл: " + name + " (" + size + " KB)");
    }

    @Override
    public int getSize() {
        return size;
    }
}

// Composite (Композит) - Папка
class Directory extends FileSystemComponent {
    private List<FileSystemComponent> children = new ArrayList<>();

    public Directory(String name) {
        super(name);
    }

    public void addComponent(FileSystemComponent component) {
        children.add(component);
    }

    public void removeComponent(FileSystemComponent component) {
        children.remove(component);
    }

    @Override
    public void display(String spacing) {
        System.out.println(spacing + "+ Папка: " + name);
        for (FileSystemComponent component : children) {
            component.display(spacing + "  ");
        }
    }

    @Override
    public int getSize() {
        int totalSize = 0;
        for (FileSystemComponent component : children) {
            totalSize += component.getSize();
        }
        return totalSize;
    }
}

// ============================================================================
// КЛИЕНТТІК КОД (MAIN)
// ============================================================================

public class M9DZ {
    public static void main(String[] args) {

        // --- 1. ФАСАД ТЕСТІ ---
        System.out.println("=== ПАТТЕРН ФАСАД ТЕСТІ ===");
        TV myTV = new TV();
        AudioSystem myAudio = new AudioSystem();
        DVDPlayer myDVD = new DVDPlayer();
        GameConsole myConsole = new GameConsole();

        HomeTheaterFacade homeTheater = new HomeTheaterFacade(myTV, myAudio, myDVD, myConsole);

        homeTheater.watchMovie();
        homeTheater.setVolume(25);
        homeTheater.listenToMusic();
        homeTheater.playGame("The Witcher 3");
        homeTheater.endMovie();

        System.out.println("\n" + "=".repeat(30) + "\n");

        // --- 2. КОМПОНОВЩИК ТЕСТІ ---
        System.out.println("=== ПАТТЕРН КОМПОНОВЩИК ТЕСТІ ===");

        // Файлдар жасау
        File doc1 = new File("report.pdf", 500);
        File img1 = new File("vacation.jpg", 2500);
        File music1 = new File("song.mp3", 4000);

        // Папкалар құрылымын жасау
        Directory root = new Directory("C:");
        Directory docsFolder = new Directory("Documents");
        Directory mediaFolder = new Directory("Media");
        Directory subMedia = new Directory("Sub-Media");

        // Иерархияны құрастыру
        root.addComponent(docsFolder);
        root.addComponent(mediaFolder);

        docsFolder.addComponent(doc1);

        mediaFolder.addComponent(img1);
        mediaFolder.addComponent(subMedia);
        subMedia.addComponent(music1);

        // Мәліметтерді шығару
        root.display("");
        System.out.println("\nЖүйенің жалпы өлшемі: " + root.getSize() + " KB");
    }
}