package modul4.dz;
import java.util.Scanner;

// ===== Интерфейс транспорта =====
interface IVehicle {
    void drive();
    void refuel();
}



// ===== Реализация Car =====
class Car implements IVehicle {
    private String brand;
    private String model;
    private String fuelType;

    public Car(String brand, String model, String fuelType) {
        this.brand = brand;
        this.model = model;
        this.fuelType = fuelType;
    }

    public void drive() {
        System.out.println("Car " + brand + " " + model + " is driving.");
    }

    public void refuel() {
        System.out.println("Car refueled with " + fuelType);
    }
}



// ===== Реализация Motorcycle =====
class Motorcycle implements IVehicle {
    private String type;
    private int engineVolume;

    public Motorcycle(String type, int engineVolume) {
        this.type = type;
        this.engineVolume = engineVolume;
    }

    public void drive() {
        System.out.println("Motorcycle (" + type + ") with " + engineVolume + "cc is driving.");
    }

    public void refuel() {
        System.out.println("Motorcycle refueled.");
    }
}



// ===== Реализация Truck =====
class Truck implements IVehicle {
    private double capacity;
    private int axles;

    public Truck(double capacity, int axles) {
        this.capacity = capacity;
        this.axles = axles;
    }

    public void drive() {
        System.out.println("Truck with capacity " + capacity + " tons and " + axles + " axles is driving.");
    }

    public void refuel() {
        System.out.println("Truck refueled.");
    }
}



// ===== Новый транспорт: Bus =====
class Bus implements IVehicle {
    private int seats;
    private String route;

    public Bus(int seats, String route) {
        this.seats = seats;
        this.route = route;
    }

    public void drive() {
        System.out.println("Bus on route " + route + " with " + seats + " seats is driving.");
    }

    public void refuel() {
        System.out.println("Bus refueled.");
    }
}



// ===== Абстрактная фабрика =====
abstract class VehicleFactory {
    public abstract IVehicle createVehicle();
}



// ===== Фабрика Car =====
class CarFactory extends VehicleFactory {
    private String brand, model, fuel;

    public CarFactory(String brand, String model, String fuel) {
        this.brand = brand;
        this.model = model;
        this.fuel = fuel;
    }

    public IVehicle createVehicle() {
        return new Car(brand, model, fuel);
    }
}



// ===== Фабрика Motorcycle =====
class MotorcycleFactory extends VehicleFactory {
    private String type;
    private int volume;

    public MotorcycleFactory(String type, int volume) {
        this.type = type;
        this.volume = volume;
    }

    public IVehicle createVehicle() {
        return new Motorcycle(type, volume);
    }
}



// ===== Фабрика Truck =====
class TruckFactory extends VehicleFactory {
    private double capacity;
    private int axles;

    public TruckFactory(double capacity, int axles) {
        this.capacity = capacity;
        this.axles = axles;
    }

    public IVehicle createVehicle() {
        return new Truck(capacity, axles);
    }
}



// ===== Фабрика Bus =====
class BusFactory extends VehicleFactory {
    private int seats;
    private String route;

    public BusFactory(int seats, String route) {
        this.seats = seats;
        this.route = route;
    }

    public IVehicle createVehicle() {
        return new Bus(seats, route);
    }
}



// ===== Главный класс =====
public class FactoryMethodTransport {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Choose vehicle type:");
        System.out.println("1 - Car");
        System.out.println("2 - Motorcycle");
        System.out.println("3 - Truck");
        System.out.println("4 - Bus");

        int choice = sc.nextInt();
        sc.nextLine();

        VehicleFactory factory = null;

        if (choice == 1) {
            System.out.print("Brand: ");
            String brand = sc.nextLine();
            System.out.print("Model: ");
            String model = sc.nextLine();
            System.out.print("Fuel type: ");
            String fuel = sc.nextLine();

            factory = new CarFactory(brand, model, fuel);
        }

        else if (choice == 2) {
            System.out.print("Motorcycle type: ");
            String type = sc.nextLine();
            System.out.print("Engine volume: ");
            int volume = sc.nextInt();

            factory = new MotorcycleFactory(type, volume);
        }

        else if (choice == 3) {
            System.out.print("Capacity (tons): ");
            double cap = sc.nextDouble();
            System.out.print("Axles: ");
            int axles = sc.nextInt();

            factory = new TruckFactory(cap, axles);
        }

        else if (choice == 4) {
            System.out.print("Seats: ");
            int seats = sc.nextInt();
            sc.nextLine();
            System.out.print("Route: ");
            String route = sc.nextLine();

            factory = new BusFactory(seats, route);
        }

        else {
            System.out.println("Wrong choice!");
            return;
        }

        // Создание транспорта через фабрику
        IVehicle vehicle = factory.createVehicle();
        vehicle.drive();
        vehicle.refuel();
    }
}