import java.util.*;

abstract class InventoryEvent {
    protected String itemName;
    protected long timestamp;

    public InventoryEvent(String itemName) {
        this.itemName = itemName;
        this.timestamp = System.currentTimeMillis();
    }

    public String getItemName() {
        return itemName;
    }

    public abstract String getDescription();
}

class ItemAddedEvent extends InventoryEvent {
    private int quantity;
    private double price;

    public ItemAddedEvent(String itemName, int quantity, double price) {
        super(itemName);
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public String getDescription() {
        return "Добавлена партия товара " + itemName + ": " + quantity + " шт. по " + price + " руб.";
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}

class ItemQuantityLowEvent extends InventoryEvent {
    private int currentQuantity;
    private int minLevel;

    public ItemQuantityLowEvent(String itemName, int currentQuantity, int minLevel) {
        super(itemName);
        this.currentQuantity = currentQuantity;
        this.minLevel = minLevel;
    }

    @Override
    public String getDescription() {
        return "Критический уровень запаса товара " + itemName + ": осталось " + currentQuantity + " шт. (минимум: " + minLevel + ")";
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }
}

class ItemMovedEvent extends InventoryEvent {
    private String fromWarehouse;
    private String toWarehouse;
    private int quantity;

    public ItemMovedEvent(String itemName, String fromWarehouse, String toWarehouse, int quantity) {
        super(itemName);
        this.fromWarehouse = fromWarehouse;
        this.toWarehouse = toWarehouse;
        this.quantity = quantity;
    }

    @Override
    public String getDescription() {
        return "Перемещение товара " + itemName + ": со склада " + fromWarehouse + " на склад " + toWarehouse + " (" + quantity + " шт.)";
    }

    public String getFromWarehouse() {
        return fromWarehouse;
    }

    public String getToWarehouse() {
        return toWarehouse;
    }
}

class ItemUpdatedEvent extends InventoryEvent {
    private String field;
    private String oldValue;
    private String newValue;

    public ItemUpdatedEvent(String itemName, String field, String oldValue, String newValue) {
        super(itemName);
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public String getDescription() {
        return "Изменение характеристик товара " + itemName + ": " + field + " (" + oldValue + " -> " + newValue + ")";
    }

    public String getField() {
        return field;
    }
}

enum UserRole {
    WAREHOUSE_MANAGER, OPERATOR, QUALITY_CONTROLLER
}

interface Notification {
    void sendNotification(String userName, String message);
}

class EmailNotification implements Notification {
    @Override
    public void sendNotification(String userName, String message) {
        System.out.println("[EMAIL] Пользователю " + userName + ": " + message);
    }
}

class SMSNotification implements Notification {
    @Override
    public void sendNotification(String userName, String message) {
        System.out.println("[SMS] Пользователю " + userName + ": " + message);
    }
}

class InAppNotification implements Notification {
    @Override
    public void sendNotification(String userName, String message) {
        System.out.println("[IN-APP] Пользователю " + userName + ": " + message);
    }
}

interface Observer {
    void update(InventoryEvent event);
}

class WarehouseUser implements Observer {
    private String name;
    private UserRole role;
    private List<Notification> notificationChannels;
    private Set<Class<?>> subscribedEventTypes;

    public WarehouseUser(String name, UserRole role) {
        this.name = name;
        this.role = role;
        this.notificationChannels = new ArrayList<>();
        this.subscribedEventTypes = new HashSet<>();
        setupRoleSubscriptions();
    }

    private void setupRoleSubscriptions() {
        switch (role) {
            case WAREHOUSE_MANAGER:
                subscribedEventTypes.add(ItemQuantityLowEvent.class);
                subscribedEventTypes.add(ItemMovedEvent.class);
                break;
            case OPERATOR:
                subscribedEventTypes.add(ItemAddedEvent.class);
                subscribedEventTypes.add(ItemUpdatedEvent.class);
                break;
            case QUALITY_CONTROLLER:
                subscribedEventTypes.add(ItemUpdatedEvent.class);
                subscribedEventTypes.add(ItemAddedEvent.class);
                subscribedEventTypes.add(ItemMovedEvent.class);
                break;
        }
    }

    public void addNotificationChannel(Notification notification) {
        notificationChannels.add(notification);
    }

    public void subscribeToEvent(Class<?> eventType) {
        subscribedEventTypes.add(eventType);
    }

    public void unsubscribeFromEvent(Class<?> eventType) {
        subscribedEventTypes.remove(eventType);
    }

    @Override
    public void update(InventoryEvent event) {
        if (subscribedEventTypes.contains(event.getClass())) {
            for (Notification channel : notificationChannels) {
                channel.sendNotification(name + " (" + role + ")", event.getDescription());
            }
        }
    }

    public String getName() {
        return name;
    }

    public UserRole getRole() {
        return role;
    }
}

interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(InventoryEvent event);
}

class InventoryItem implements Subject {
    private String name;
    private int quantity;
    private String warehouse;
    private String manufacturer;
    private List<Observer> observers;
    private int minQuantityLevel;

    public InventoryItem(String name, int quantity, String warehouse, String manufacturer, int minLevel) {
        this.name = name;
        this.quantity = quantity;
        this.warehouse = warehouse;
        this.manufacturer = manufacturer;
        this.minQuantityLevel = minLevel;
        this.observers = new ArrayList<>();
    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(InventoryEvent event) {
        for (Observer observer : observers) {
            observer.update(event);
        }
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
        notifyObservers(new ItemAddedEvent(name, amount, 0));
    }

    public void removeQuantity(int amount) {
        this.quantity -= amount;
        if (this.quantity < minQuantityLevel) {
            notifyObservers(new ItemQuantityLowEvent(name, this.quantity, minQuantityLevel));
        }
    }

    public void moveToWarehouse(String toWarehouse, int amount) {
        String oldWarehouse = this.warehouse;
        this.warehouse = toWarehouse;
        notifyObservers(new ItemMovedEvent(name, oldWarehouse, toWarehouse, amount));
    }

    public void updateManufacturer(String newManufacturer) {
        String oldManufacturer = this.manufacturer;
        this.manufacturer = newManufacturer;
        notifyObservers(new ItemUpdatedEvent(name, "Производитель", oldManufacturer, newManufacturer));
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getWarehouse() {
        return warehouse;
    }
}

public class modul06dz {
    public static void main(String[] args) {
        InventoryItem laptop = new InventoryItem("Ноутбук Dell XPS 13", 50, "Склад 1", "Dell", 10);
        InventoryItem phone = new InventoryItem("Смартфон Samsung Galaxy", 30, "Склад 2", "Samsung", 5);

        WarehouseUser warehouseManager = new WarehouseUser("Талапхан Е", UserRole.WAREHOUSE_MANAGER);
        WarehouseUser operator = new WarehouseUser("Сергей Лазеров", UserRole.OPERATOR);
        WarehouseUser qualityController = new WarehouseUser("Сергей Иванов", UserRole.QUALITY_CONTROLLER);

        warehouseManager.addNotificationChannel(new EmailNotification());
        warehouseManager.addNotificationChannel(new SMSNotification());

        operator.addNotificationChannel(new EmailNotification());
        operator.addNotificationChannel(new InAppNotification());

        qualityController.addNotificationChannel(new InAppNotification());

        laptop.attach(warehouseManager);
        laptop.attach(operator);
        laptop.attach(qualityController);

        phone.attach(warehouseManager);
        phone.attach(operator);
        phone.attach(qualityController);

        System.out.println("=== Добавление новой партии ===");
        laptop.addQuantity(20);

        System.out.println("\n=== Снижение запаса ===");
        phone.removeQuantity(28);

        System.out.println("\n=== Перемещение товара ===");
        laptop.moveToWarehouse("Склад 2", 15);

        System.out.println("\n=== Изменение производителя ===");
        phone.updateManufacturer("Apple");

        System.out.println("\n=== Отписка менеджера от событий перемещения ===");
        warehouseManager.unsubscribeFromEvent(ItemMovedEvent.class);

        System.out.println("\n=== Еще одно перемещение ===");
        laptop.moveToWarehouse("Склад 1", 10);
    }
}
