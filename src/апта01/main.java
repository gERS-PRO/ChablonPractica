package апта01;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

abstract class Vehicle {
    protected String brand;
    protected String model;
    protected int year;

    public Vehicle(String brand, String model, int year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    public void startEngine() {
        System.out.println("Двигатель " + brand + " " + model + " заведен.");
    }

    public void stopEngine() {
        System.out.println("Двигатель " + brand + " " + model + " остановлен.");
    }

    public String getInfo() {
        return year + " " + brand + " " + model;
    }

    public String getModel() { return model; }
}

class Car extends Vehicle {
    private int doorsCount;
    private String transmissionType;

    public Car(String brand, String model, int year, int doorsCount, String transmissionType) {
        super(brand, model, year);
        this.doorsCount = doorsCount;
        this.transmissionType = transmissionType;
    }

    @Override
    public String getInfo() {
        return super.getInfo() + " [Автомобиль, Дверей: " + doorsCount + ", КПП: " + transmissionType + "]";
    }
}

class Motorcycle extends Vehicle {
    private String bodyType;
    private boolean hasBox;

    public Motorcycle(String brand, String model, int year, String bodyType, boolean hasBox) {
        super(brand, model, year);
        this.bodyType = bodyType;
        this.hasBox = hasBox;
    }

    @Override
    public String getInfo() {
        return super.getInfo() + " [Мотоцикл, Тип: " + bodyType + ", Кофр: " + (hasBox ? "Есть" : "Нет") + "]";
    }
}
class Garage {
    private String name;
    private List<Vehicle> vehicles;

    public Garage(String name) {
        this.name = name;
        this.vehicles = new ArrayList<>();
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        System.out.println("✓ Транспорт " + vehicle.getModel() + " добавлен в гараж: " + name);
    }

    public boolean removeVehicle(String model) {
        return vehicles.removeIf(v -> v.getModel().equalsIgnoreCase(model));
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public String getName() { return name; }
}

class Fleet {
    private List<Garage> garages;

    public Fleet() {
        this.garages = new ArrayList<>();
    }

    public void addGarage(Garage garage) {
        garages.add(garage);
        System.out.println("✓ Гараж '" + garage.getName() + "' добавлен в автопарк.");
    }

    public boolean removeGarage(String name) {
        return garages.removeIf(g -> g.getName().equalsIgnoreCase(name));
    }

    // Поиск транспортного средства по модели во всех гаражах
    public void findVehicle(String model) {
        System.out.println("\nПоиск модели '" + model + "' в автопарке:");
        boolean found = false;
        for (Garage g : garages) {
            for (Vehicle v : g.getVehicles()) {
                if (v.getModel().equalsIgnoreCase(model)) {
                    System.out.println("- Найдено в гараже '" + g.getName() + "': " + v.getInfo());
                    found = true;
                }
            }
        }
        if (!found) System.out.println("✗ Ничего не найдено.");
    }
}

public class main {
    public static void main(String[] args) {
        runAutomatedTest();
    }

    public static void runAutomatedTest() {
        System.out.println("=== ЗАПУСК ТЕСТИРОВАНИЯ АВТОПАРКА ===");

        // 1. Создание транспорта
        Car car1 = new Car("Toyota", "Camry", 2022, 4, "Автомат");
        Car car2 = new Car("Tesla", "Model 3", 2023, 4, "Редуктор");
        Motorcycle bike1 = new Motorcycle("Harley", "davidson", 2021, "Cruiser", false);

        // 2. Создание гаражей и добавление транспорта (Композиция)
        Garage cityGarage = new Garage("Городской Гараж");
        cityGarage.addVehicle(car1);
        cityGarage.addVehicle(bike1);

        Garage privateGarage = new Garage("Частный Гараж");
        privateGarage.addVehicle(car2);

        // 3. Создание автопарка и добавление гаражей (Композиция)
        Fleet myFleet = new Fleet();
        myFleet.addGarage(cityGarage);
        myFleet.addGarage(privateGarage);

        // 4. Проверка работы двигателей (Полиморфизм)
        System.out.println("\n--- Проверка двигателей ---");
        car1.startEngine();
        bike1.startEngine();
        bike1.stopEngine();

        // 5. Поиск транспорта
        myFleet.findVehicle("Camry");
        myFleet.findVehicle("Model 3");

        // 6. Тест удаления транспорта
        System.out.println("\n--- Удаление транспорта ---");
        boolean removed = cityGarage.removeVehicle("Iron 883");
        System.out.println("Удаление Iron 883 из городского гаража: " + (removed ? "Успешно" : "Ошибка"));

        // 7. Тест удаления гаража
        System.out.println("\n--- Удаление гаража ---");
        boolean garageRemoved = myFleet.removeGarage("Частный Гараж");
        System.out.println("Удаление 'Частный Гараж' из автопарка: " + (garageRemoved ? "Успешно" : "Ошибка"));

        // Итоговый поиск
        myFleet.findVehicle("Model 3"); // Должно быть не найдено, так как гараж удален

        System.out.println("\n=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===");
    }
}