package modul3.dz;
public class ISPExample {
    public static void main(String[] args) {
        Printer printer = new BasicPrinter();
        printer.print("Hello");

        AllInOnePrinter all = new AllInOnePrinter();
        all.scan("Document");
    }
}

// Бөлек интерфейстер
interface Printer {
    void print(String text);
}

interface ScannerDevice {
    void scan(String text);
}

interface FaxDevice {
    void fax(String text);
}

// Көп функциялы принтер
class AllInOnePrinter implements Printer, ScannerDevice, FaxDevice {
    public void print(String text) {
        System.out.println("Print: " + text);
    }

    public void scan(String text) {
        System.out.println("Scan: " + text);
    }

    public void fax(String text) {
        System.out.println("Fax: " + text);
    }
}

// Қарапайым принтер
class BasicPrinter implements Printer {
    public void print(String text) {
        System.out.println("Print: " + text);
    }
}

