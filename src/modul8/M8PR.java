package modul8;
import java.util.*;

// ======================================================
// ЧАСТЬ 1: ПАТТЕРН ДЕКОРАТОР (Система отчетности)
// ======================================================

// 1. Интерфейс для всех отчетов
interface IReport {
    String generate();
}

// 2. Базовый отчет по продажам
class SalesReport implements IReport {
    @Override
    public String generate() {
        return "Данные отчета по ПРОДАЖАМ: [Товар А: 100$, Товар Б: 200$, Товар В: 50$]";
    }
}

// 2. Базовый отчет по пользователям
class UserReport implements IReport {
    @Override
    public String generate() {
        return "Данные отчета по ПОЛЬЗОВАТЕЛЯМ: [Иван (Admin), Мария (User), Петр (User)]";
    }
}

// 3. Абстрактный декоратор
abstract class ReportDecorator implements IReport {
    protected IReport report;

    public ReportDecorator(IReport report) {
        this.report = report;
    }

    @Override
    public String generate() {
        return report.generate();
    }
}

// 4. Конкретные декораторы
class DateFilterDecorator extends ReportDecorator {
    public DateFilterDecorator(IReport report) { super(report); }
    @Override
    public String generate() {
        return report.generate() + " + [Фильтр: за март 2024]";
    }
}

class SortingDecorator extends ReportDecorator {
    public SortingDecorator(IReport report) { super(report); }
    @Override
    public String generate() {
        return report.generate() + " + [Сортировка: по возрастанию цены]";
    }
}

class CsvExportDecorator extends ReportDecorator {
    public CsvExportDecorator(IReport report) { super(report); }
    @Override
    public String generate() {
        return "CSV_FORMAT(" + report.generate() + ")";
    }
}

class PdfExportDecorator extends ReportDecorator {
    public PdfExportDecorator(IReport report) { super(report); }
    @Override
    public String generate() {
        return "PDF_DOCUMENT(" + report.generate() + ")";
    }
}

// Новый декоратор (доп. задание): Фильтр по сумме
class AmountFilterDecorator extends ReportDecorator {
    public AmountFilterDecorator(IReport report) { super(report); }
    @Override
    public String generate() {
        return report.generate() + " + [Фильтр: сумма > 100$]";
    }
}

// ======================================================
// ЧАСТЬ 2: ПАТТЕРН АДАПТЕР (Логистика)
// ======================================================

// 1. Интерфейс внутренней службы доставки
interface IInternalDeliveryService {
    void deliverOrder(String orderId);
    String getDeliveryStatus(String orderId);
    double calculateCost(String orderId); // Доп. функционал
}

// 2. Реализация внутренней службы
class InternalDeliveryService implements IInternalDeliveryService {
    @Override
    public void deliverOrder(String orderId) {
        System.out.println("Внутренняя служба: Доставка заказа " + orderId + " начата.");
    }
    @Override
    public String getDeliveryStatus(String orderId) {
        return "В пути (Внутренняя)";
    }
    @Override
    public double calculateCost(String orderId) { return 500.0; }
}

// 3. Сторонние службы с другими интерфейсами
class ExternalLogisticsServiceA {
    public void shipItem(int itemId) { System.out.println("Service A: Отгрузка товара №" + itemId); }
    public String trackShipment(int shipmentId) { return "Status A-100"; }
}

class ExternalLogisticsServiceB {
    public void sendPackage(String info) { System.out.println("Service B: Посылка отправлена: " + info); }
    public String checkPackageStatus(String code) { return "Status B-OK"; }
}

// Новая сторонняя служба (доп. задание)
class ExternalLogisticsServiceC {
    public void dispatch(String id, double weight) { System.out.println("Service C: Экспресс-доставка " + id + ", вес: " + weight); }
}

// 4. Адаптеры
class LogisticsAdapterA implements IInternalDeliveryService {
    private ExternalLogisticsServiceA serviceA = new ExternalLogisticsServiceA();
    @Override
    public void deliverOrder(String orderId) {
        // Логирование и обработка ошибок (доп. задание)
        System.out.println("[LOG]: Адаптация вызова для Service A...");
        try {
            int id = Integer.parseInt(orderId);
            serviceA.shipItem(id);
        } catch (Exception e) { System.err.println("Ошибка адаптации ID!"); }
    }
    @Override
    public String getDeliveryStatus(String orderId) { return "Статус от A: " + serviceA.trackShipment(123); }
    @Override
    public double calculateCost(String orderId) { return 1200.0; }
}

class LogisticsAdapterB implements IInternalDeliveryService {
    private ExternalLogisticsServiceB serviceB = new ExternalLogisticsServiceB();
    @Override
    public void deliverOrder(String orderId) {
        serviceB.sendPackage("ID_Заказа: " + orderId);
    }
    @Override
    public String getDeliveryStatus(String orderId) { return "Статус от B: " + serviceB.checkPackageStatus(orderId); }
    @Override
    public double calculateCost(String orderId) { return 850.0; }
}

class LogisticsAdapterC implements IInternalDeliveryService {
    private ExternalLogisticsServiceC serviceC = new ExternalLogisticsServiceC();
    @Override
    public void deliverOrder(String orderId) { serviceC.dispatch(orderId, 5.5); }
    @Override
    public String getDeliveryStatus(String orderId) { return "Статус от C: Активен"; }
    @Override
    public double calculateCost(String orderId) { return 2000.0; }
}

// 5. Фабрика для выбора службы
class DeliveryServiceFactory {
    public static IInternalDeliveryService getDeliveryService(String type) {
        switch (type.toLowerCase()) {
            case "internal": return new InternalDeliveryService();
            case "external_a": return new LogisticsAdapterA();
            case "external_b": return new LogisticsAdapterB();
            case "external_c": return new LogisticsAdapterC();
            default: throw new IllegalArgumentException("Неизвестный тип службы!");
        }
    }
}

// ======================================================
// ГЛАВНЫЙ КЛАСС (Демонстрация)
// ======================================================
public class M8PR {
    public static void main(String[] args) {
        // --- ТЕСТ ДЕКОРАТОРА ---
        System.out.println("=== ТЕСТ ПАТТЕРНА ДЕКОРАТОР ===");
        IReport report = new SalesReport(); // Базовый отчет
        report = new DateFilterDecorator(report); // Навешиваем фильтр дат
        report = new AmountFilterDecorator(report); // Навешиваем фильтр суммы
        report = new SortingDecorator(report); // Сортируем
        report = new PdfExportDecorator(report); // Экспортируем в PDF

        System.out.println(report.generate());

        System.out.println("\n=== ТЕСТ ПАТТЕРНА АДАПТЕР + ФАБРИКА ===");
        // Пример динамического выбора службы через фабрику
        String[] servicesToTest = {"internal", "external_a", "external_c"};

        for (String type : servicesToTest) {
            IInternalDeliveryService service = DeliveryServiceFactory.getDeliveryService(type);
            System.out.println("--- Работа со службой: " + type + " ---");
            service.deliverOrder("999");
            System.out.println("Статус: " + service.getDeliveryStatus("999"));
            System.out.println("Стоимость: " + service.calculateCost("999") + " руб.");
        }
    }
}
