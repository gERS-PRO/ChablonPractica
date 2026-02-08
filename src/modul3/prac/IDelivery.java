package modul3.prac;

public interface IDelivery {
    void deliverOrder(Order order);
}

class CourierDelivery implements IDelivery {
    public void deliverOrder(Order order) {
        System.out.println("Delivered by Courier");
    }
}

class PostDelivery implements IDelivery {
    public void deliverOrder(Order order) {
        System.out.println("Delivered by Post");
    }
}

class PickUpPointDelivery implements IDelivery {
    public void deliverOrder(Order order) {
        System.out.println("Delivered to Pick-Up Point");
    }
}
