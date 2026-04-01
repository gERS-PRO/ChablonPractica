package modul9;
import java.util.*;

// ============================================================================
// 1. ПАТТЕРН ФАСАД (FACADE) - Қонақ үйді басқару жүйесі
// ============================================================================

// --- Кіші жүйелер (Subsystems) ---

class RoomBookingSystem {
    public void bookRoom(String type) { System.out.println("Нөмір брондалды: " + type); }
    public void cancelBooking() { System.out.println("Нөмір броны жойылды."); }
    public boolean checkAvailability(String type) { return true; }
}

class RestaurantSystem {
    public void reserveTable(int people) { System.out.println(people + " адамға үстел дайындалды."); }
    public void orderFood(String dish) { System.out.println("Тағамға тапсырыс қабылданды: " + dish); }
    public void callTaxi() { System.out.println("Мейрамханадан такси шақырылды."); }
}

class EventManagementSystem {
    public void bookConferenceHall(String name) { System.out.println("Конференция залы брондалды: " + name); }
    public void orderEquipment(String item) { System.out.println("Жабдық дайындалды: " + item); }
}

class CleaningService {
    public void scheduleCleaning(int roomNumber) { System.out.println(roomNumber + "-нөмірге тазалау кестесі қойылды."); }
    public void cleanNow(int roomNumber) { System.out.println(roomNumber + "-нөмір шұғыл тазаланып жатыр."); }
}

// --- Фасад класы (HotelFacade) ---



class HotelFacade {
    private RoomBookingSystem rooms = new RoomBookingSystem();
    private RestaurantSystem restaurant = new RestaurantSystem();
    private EventManagementSystem events = new EventManagementSystem();
    private CleaningService cleaning = new CleaningService();

    // Кешенді қызметтер
    public void bookRoomWithServices(String roomType, String dish, int roomNumber) {
        System.out.println("\n--- Бөлме + Тамақ + Тазалау сценарийі іске қосылуда ---");
        rooms.bookRoom(roomType);
        restaurant.orderFood(dish);
        cleaning.scheduleCleaning(roomNumber);
    }

    public void organizeEvent(String hallName, String equipment, int participantRooms) {
        System.out.println("\n--- Іс-шара ұйымдастыру сценарийі іске қосылуда ---");
        events.bookConferenceHall(hallName);
        events.orderEquipment(equipment);
        for (int i = 0; i < participantRooms; i++) {
            rooms.bookRoom("Стандартты");
        }
    }

    public void bookTableWithTaxi(int people) {
        System.out.println("\n--- Үстел + Такси сценарийі іске қосылуда ---");
        restaurant.reserveTable(people);
        restaurant.callTaxi();
    }

    // Қосымша функциялар
    public void cancelAllReservations() {
        System.out.println("\n--- Барлық брондарды жою ---");
        rooms.cancelBooking();
    }

    public void requestUrgentCleaning(int roomNumber) {
        System.out.println("\n--- Шұғыл тазалау сұранысы ---");
        cleaning.cleanNow(roomNumber);
    }
}

// ============================================================================
// 2. ПАТТЕРН КОМПОНОВЩИК (COMPOSITE) - Корпоративтік құрылым
// ============================================================================



abstract class OrganizationComponent {
    protected String name;
    public OrganizationComponent(String name) { this.name = name; }
    public String getName() { return name; }

    public abstract void print(String spacing);
    public abstract double getBudget();
    public abstract int getEmployeeCount();


    public abstract OrganizationComponent findEmployee(String name);
}

// --- Leaf: Қызметкерлер ---
class Employee extends OrganizationComponent {
    private String position;
    private double salary;

    public Employee(String name, String position, double salary) {
        super(name);
        this.position = position;
        this.salary = salary;
    }

    public void setSalary(double salary) { this.salary = salary; }

    @Override
    public void print(String spacing) {
        System.out.println(spacing + "- [Қызметкер] " + name + " (" + position + "), Жалақы: " + salary);
    }

    @Override
    public double getBudget() { return salary; }

    @Override
    public int getEmployeeCount() { return 1; }

    @Override
    public OrganizationComponent findEmployee(String name) {
        return this.name.equalsIgnoreCase(name) ? this : null;
    }

    @Override
    public String toString() {
        return "Аты: " + name + ", Лауазымы: " + position + ", Жалақы: " + salary;
    }
}


class Contractor extends OrganizationComponent {
    private double fixedFee;

    public Contractor(String name, double fixedFee) {
        super(name);
        this.fixedFee = fixedFee;
    }

    @Override
    public void print(String spacing) {
        System.out.println(spacing + "- [Контрактор] " + name + " (Уақытша), Төлем: " + fixedFee);
    }

    @Override
    public double getBudget() { return 0; } // Бюджетке қосылмайды

    @Override
    public int getEmployeeCount() { return 1; }

    @Override
    public OrganizationComponent findEmployee(String name) {
        return this.name.equalsIgnoreCase(name) ? this : null;
    }
}

// --- Composite: Бөлімдер ---
class Department extends OrganizationComponent {
    private List<OrganizationComponent> components = new ArrayList<>();

    public Department(String name) { super(name); }

    public void addComponent(OrganizationComponent component) { components.add(component); }
    public void removeComponent(OrganizationComponent component) { components.remove(component); }

    @Override
    public void print(String spacing) {
        System.out.println(spacing + "+ [Бөлім] " + name);
        for (OrganizationComponent c : components) {
            c.print(spacing + "  ");
        }
    }

    @Override
    public double getBudget() {
        double total = 0;
        for (OrganizationComponent c : components) total += c.getBudget();
        return total;
    }

    @Override
    public int getEmployeeCount() {
        int count = 0;
        for (OrganizationComponent c : components) count += c.getEmployeeCount();
        return count;
    }

    @Override
    public OrganizationComponent findEmployee(String name) {
        for (OrganizationComponent c : components) {
            OrganizationComponent found = c.findEmployee(name);
            if (found != null) return found;
        }
        return null;
    }

    public void listAllEmployees() {
        System.out.println("\n--- " + name + " бөлімінің барлық қызметкерлері ---");
        this.print("");
    }
}

// ============================================================================
// 3. КЛИЕНТТІК КОД
// ============================================================================

public class M9PR {
    public static void main(String[] args) {
        // --- ФАСАД ТЕСТІ ---
        System.out.println("=== 1. ПАТТЕРН ФАСАД ТЕСТІ ===");
        HotelFacade hotel = new HotelFacade();

        hotel.bookRoomWithServices("Люкс", "Стейк", 101);
        hotel.organizeEvent("Main Hall", "Проектор", 2);
        hotel.bookTableWithTaxi(4);
        hotel.requestUrgentCleaning(101);

        // --- КОМПОНОВЩИК ТЕСТІ ---
        System.out.println("\n\n=== 2. ПАТТЕРН КОМПОНОВЩИК ТЕСТІ ===");

        // Құрылым жасау
        Department headOffice = new Department("Бас кеңсе");
        Department itDept = new Department("IT Бөлімі");
        Department hrDept = new Department("HR Бөлімі");

        Employee emp1 = new Employee("Азамат", "Директор", 1000000);
        Employee emp2 = new Employee("Динара", "Программист", 600000);
        Employee emp3 = new Employee("Серік", "QA Инженер", 400000);
        Contractor cont1 = new Contractor("Тимур", 200000); // Бюджетке кірмейді

        // Иерархияны құрастыру
        headOffice.addComponent(emp1);
        headOffice.addComponent(itDept);
        headOffice.addComponent(hrDept);

        itDept.addComponent(emp2);
        itDept.addComponent(emp3);
        itDept.addComponent(cont1);

        hrDept.addComponent(new Employee("Айжан", "Рекрутер", 300000));

        // Нәтижелерді шығару
        headOffice.print("");
        System.out.println("\nЖалпы бюджет: " + headOffice.getBudget());
        System.out.println("Жалпы қызметкерлер саны: " + headOffice.getEmployeeCount());

        // Іздеу және Жалақы өзгерту
        System.out.println("\n--- Іздеу және өзгерту ---");
        OrganizationComponent found = headOffice.findEmployee("Динара");
        if (found instanceof Employee) {
            System.out.println("Табылды: " + found);
            ((Employee) found).setSalary(700000);
            System.out.println("Жалақы жаңартылды. Жаңа жалпы бюджет: " + headOffice.getBudget());
        }

        // Тізімді шығару
        itDept.listAllEmployees();
    }
}