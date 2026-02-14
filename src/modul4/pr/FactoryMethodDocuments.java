package modul4.pr;
import java.util.Scanner;

// ===== Интерфейс Document =====
interface Document {
    void open();
}



// ===== Конкретные документы =====
class Report implements Document {
    public void open() {
        System.out.println("Opening Report document...");
    }
}

class Resume implements Document {
    public void open() {
        System.out.println("Opening Resume document...");
    }
}

class Letter implements Document {
    public void open() {
        System.out.println("Opening Letter document...");
    }
}

// Новый документ Invoice
class Invoice implements Document {
    public void open() {
        System.out.println("Opening Invoice document...");
    }
}



// ===== Абстрактный создатель =====
abstract class DocumentCreator {
    public abstract Document createDocument();
}



// ===== Конкретные фабрики =====
class ReportCreator extends DocumentCreator {
    public Document createDocument() {
        return new Report();
    }
}

class ResumeCreator extends DocumentCreator {
    public Document createDocument() {
        return new Resume();
    }
}

class LetterCreator extends DocumentCreator {
    public Document createDocument() {
        return new Letter();
    }
}

class InvoiceCreator extends DocumentCreator {
    public Document createDocument() {
        return new Invoice();
    }
}



// ===== Главный класс =====
public class FactoryMethodDocuments {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Choose document type:");
        System.out.println("1 - Report");
        System.out.println("2 - Resume");
        System.out.println("3 - Letter");
        System.out.println("4 - Invoice");

        int choice = sc.nextInt();

        DocumentCreator creator = null;

        if (choice == 1) {
            creator = new ReportCreator();
        }
        else if (choice == 2) {
            creator = new ResumeCreator();
        }
        else if (choice == 3) {
            creator = new LetterCreator();
        }
        else if (choice == 4) {
            creator = new InvoiceCreator();
        }
        else {
            System.out.println("Wrong choice!");
            return;
        }

        // Фабричный метод
        Document doc = creator.createDocument();
        doc.open();
    }
}