package s1;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;


interface Borrowable {
    void borrowItem();
    void returnItem();
    boolean isAvailable();
    int getAvailableCount();
}


abstract class LibraryItem {
    protected String title;
    protected String itemId;
    protected int totalCount;
    protected int availableCount;

    public LibraryItem(String title, String itemId, int totalCount) {
        this.title = title;
        this.itemId = itemId;
        this.totalCount = totalCount;
        this.availableCount = totalCount;
    }

    public abstract String getDetails();

    public String getTitle() { return title; }
    public String getItemId() { return itemId; }
    public int getTotalCount() { return totalCount; }
    public int getAvailableCount() { return availableCount; }

    public void addCopies(int count) {
        this.totalCount += count;
        this.availableCount += count;
        System.out.println("✓ Добавлено " + count + " экз. Всего: " + totalCount);
    }
}


class Book extends LibraryItem implements Borrowable {
    private String author;
    private String isbn;

    public Book(String title, String itemId, String author, String isbn, int totalCount) {
        super(title, itemId, totalCount);
        this.author = author;
        this.isbn = isbn;
    }

    @Override
    public String getDetails() {
        return "Книга: " + title + " | Автор: " + author + " | ISBN: " + isbn +
                " | Доступно: " + availableCount + "/" + totalCount;
    }

    @Override
    public void borrowItem() { if (availableCount > 0) availableCount--; }

    @Override
    public void returnItem() { if (availableCount < totalCount) availableCount++; }

    @Override
    public boolean isAvailable() { return availableCount > 0; }
}


class Magazine extends LibraryItem implements Borrowable {
    private int issueNumber;

    public Magazine(String title, String itemId, int issueNumber, int totalCount) {
        super(title, itemId, totalCount);
        this.issueNumber = issueNumber;
    }

    @Override
    public String getDetails() {
        return "Журнал: " + title + " | Выпуск №" + issueNumber +
                " | Доступно: " + availableCount + "/" + totalCount;
    }

    @Override
    public void borrowItem() { if (availableCount > 0) availableCount--; }

    @Override
    public void returnItem() { if (availableCount < totalCount) availableCount++; }

    @Override
    public boolean isAvailable() { return availableCount > 0; }
}


class Reader {
    private String name;
    private String readerId;
    private List<LibraryItem> borrowedItems;

    public Reader(String name, String readerId) {
        this.name = name;
        this.readerId = readerId;
        this.borrowedItems = new ArrayList<>();
    }

    public void borrowItem(LibraryItem item) {
        if (item instanceof Borrowable && ((Borrowable) item).isAvailable()) {
            ((Borrowable) item).borrowItem();
            borrowedItems.add(item);
            System.out.println("✓ " + name + " взял: " + item.getTitle());
        } else {
            System.out.println("✗ Ошибка: Предмет недоступен.");
        }
    }

    public void returnItem(LibraryItem item) {
        if (borrowedItems.remove(item)) {
            ((Borrowable) item).returnItem();
            System.out.println("✓ " + name + " вернул: " + item.getTitle());
        } else {
            System.out.println("✗ Ошибка: Читатель не брал этот предмет.");
        }
    }

    public String getName() { return name; }
    public String getReaderId() { return readerId; }

    @Override
    public String toString() {
        return "Читатель: " + name + " (ID: " + readerId + ") | На руках: " + borrowedItems.size();
    }
}


class Library {
    private List<LibraryItem> items = new ArrayList<>();
    private List<Reader> readers = new ArrayList<>();


    public void addItem(LibraryItem item) {
        items.add(item);
        System.out.println("✓ В базу добавлен предмет: " + item.getTitle());
    }


    public boolean removeItem(String itemId) {
        return items.removeIf(item -> item.getItemId().equals(itemId));
    }


    public void registerReader(Reader reader) {
        readers.add(reader);
        System.out.println("✓ Зарегистрирован читатель: " + reader.getName());
    }


    public boolean removeReader(String readerId) {
        return readers.removeIf(reader -> reader.getReaderId().equals(readerId));
    }

    public LibraryItem findItem(String itemId) {
        for (LibraryItem item : items) {
            if (item.getItemId().equals(itemId)) return item;
        }
        return null;
    }

    public Reader findReader(String readerId) {
        for (Reader reader : readers) {
            if (reader.getReaderId().equals(readerId)) return reader;
        }
        return null;
    }

    public void displayAllItems() {
        System.out.println("\n--- ФОНД БИБЛИОТЕКИ ---");
        if (items.isEmpty()) System.out.println("Библиотека пуста.");
        for (LibraryItem item : items) System.out.println(item.getDetails());
    }

    public void displayAllReaders() {
        System.out.println("\n--- ЗАРЕГИСТРИРОВАННЫЕ ЧИТАТЕЛИ ---");
        if (readers.isEmpty()) System.out.println("Читателей нет.");
        for (Reader r : readers) System.out.println(r);
    }
}


public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static Library library = new Library();

    public static void main(String[] args) {
        runAutomatedTest();
        addSampleData(); // Предварительные данные для теста

        while (true) {
            System.out.println("\n=== СИСТЕМА УПРАВЛЕНИЯ БИБЛИОТЕКОЙ ===");
            System.out.println("1. Добавить предмет (Книга/Журнал)");
            System.out.println("2. УДАЛИТЬ предмет по ID");
            System.out.println("3. Зарегистрировать читателя");
            System.out.println("4. УДАЛИТЬ читателя по ID");
            System.out.println("5. Выдать предмет читателю");
            System.out.println("6. Принять возврат предмета");
            System.out.println("7. Показать все книги/журналы");
            System.out.println("8. Показать всех читателей");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> addNewItem();
                    case 2 -> deleteItem();
                    case 3 -> registerReader();
                    case 4 -> deleteReader();
                    case 5 -> processBorrow();
                    case 6 -> processReturn();
                    case 7 -> library.displayAllItems();
                    case 8 -> library.displayAllReaders();
                    case 0 -> {
                        System.out.println("Завершение работы...");
                        return;
                    }
                    default -> System.out.println("✗ Неверный выбор.");
                }
            } catch (Exception e) {
                System.out.println("✗ Ошибка ввода. Введите число.");
            }
        }
    }

    private static void addSampleData() {
        library.addItem(new Book("Война и мир", "B1", "Л. Толстой", "123-456", 2));
        library.addItem(new Magazine("Наука и жизнь", "M1", 5, 3));
        library.registerReader(new Reader("Иван Иванов", "R1"));
    }

    private static void addNewItem() {
        System.out.print("Тип (1-Книга, 2-Журнал): ");
        int type = Integer.parseInt(scanner.nextLine());
        System.out.print("Название: "); String title = scanner.nextLine();
        System.out.print("ID: "); String id = scanner.nextLine();
        System.out.print("Кол-во: "); int count = Integer.parseInt(scanner.nextLine());

        if (type == 1) {
            System.out.print("Автор: "); String author = scanner.nextLine();
            System.out.print("ISBN: "); String isbn = scanner.nextLine();
            library.addItem(new Book(title, id, author, isbn, count));
        } else {
            System.out.print("Номер выпуска: "); int num = Integer.parseInt(scanner.nextLine());
            library.addItem(new Magazine(title, id, num, count));
        }
    }

    private static void deleteItem() {
        System.out.print("Введите ID предмета для удаления: ");
        String id = scanner.nextLine();
        if (library.removeItem(id)) System.out.println("✓ Предмет успешно удален.");
        else System.out.println("✗ Предмет с таким ID не найден.");
    }

    private static void registerReader() {
        System.out.print("Имя читателя: "); String name = scanner.nextLine();
        System.out.print("ID читателя: "); String id = scanner.nextLine();
        library.registerReader(new Reader(name, id));
    }

    private static void deleteReader() {
        System.out.print("Введите ID читателя для удаления: ");
        String id = scanner.nextLine();
        if (library.removeReader(id)) System.out.println("✓ Читатель удален.");
        else System.out.println("✗ Читатель не найден.");
    }

    private static void processBorrow() {
        System.out.print("ID читателя: "); String rId = scanner.nextLine();
        System.out.print("ID предмета: "); String iId = scanner.nextLine();
        Reader r = library.findReader(rId);
        LibraryItem i = library.findItem(iId);
        if (r != null && i != null) r.borrowItem(i);
        else System.out.println("✗ Ошибка: Читатель или предмет не найдены.");
    }

    private static void processReturn() {
        System.out.print("ID читателя: "); String rId = scanner.nextLine();
        System.out.print("ID предмета: "); String iId = scanner.nextLine();
        Reader r = library.findReader(rId);
        LibraryItem i = library.findItem(iId);
        if (r != null && i != null) r.returnItem(i);
        else System.out.println("✗ Ошибка данных.");
    }
    private static void runAutomatedTest() {
        System.out.println("\n===== ЗАПУСК АВТОМАТИЧЕСКОГО ТЕСТИРОВАНИЯ =====");


        Book testBook = new Book("Тестовая Книга", "T1", "Автор Тест", "000-000", 2);
        library.addItem(testBook);

        Reader testReader = new Reader("Тестовый Читатель", "TR1");
        library.registerReader(testReader);


        System.out.println("\n--- Тест выдачи ---");
        testReader.borrowItem(testBook); // Должно сработать, осталось 1
        System.out.println("Осталось в библиотеке: " + testBook.getAvailableCount());


        System.out.println("\n--- Тест возврата ---");
        testReader.returnItem(testBook); // Должно сработать, снова 2
        System.out.println("Осталось в библиотеке: " + testBook.getAvailableCount());


        System.out.println("\n--- Тест удаления ---");
        boolean removed = library.removeItem("T1");
        System.out.println("Результат удаления книги T1: " + (removed ? "Успешно" : "Ошибка"));


        boolean readerRemoved = library.removeReader("TR1");
        System.out.println("Результат удаления читателя TR1: " + (readerRemoved ? "Успешно" : "Ошибка"));

        System.out.println("===== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО =====\n");
    }
}