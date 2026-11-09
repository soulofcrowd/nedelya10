import java.util.ArrayList;
import java.util.List;

// ===== Стратегия =====
interface DeliveryStrategy {
    String getName();
    double calculateCost(double weightKg, double distanceKm);
}

class StandardDelivery implements DeliveryStrategy {
    @Override
    public String getName() {
        return "Обычная доставка";
    }

    @Override
    public double calculateCost(double weightKg, double distanceKm) {
        return 500 + weightKg * 50 + distanceKm * 10;
    }
}

class ExpressDelivery implements DeliveryStrategy {
    @Override
    public String getName() {
        return "Экспресс-доставка";
    }

    @Override
    public double calculateCost(double weightKg, double distanceKm) {
        return 1000 + weightKg * 100 + distanceKm * 25;
    }
}

class PickupDelivery implements DeliveryStrategy {
    @Override
    public String getName() {
        return "Самовывоз";
    }

    @Override
    public double calculateCost(double weightKg, double distanceKm) {
        return 0;
    }
}

// Новая стратегия (доказательство OCP): остальной код не менялся
class DroneDelivery implements DeliveryStrategy {
    @Override
    public String getName() {
        return "Доставка дроном";
    }

    @Override
    public double calculateCost(double weightKg, double distanceKm) {
        return 1500 + weightKg * 200 + distanceKm * 5;
    }
}

// ===== Заказ: хранит данные, расчёт делегирует стратегии (SRP) =====
class Order {
    private final int id;
    private final String customerName;
    private final double weightKg;
    private final double distanceKm;
    private DeliveryStrategy deliveryStrategy;

    public Order(int id, String customerName, double weightKg, double distanceKm,
                 DeliveryStrategy deliveryStrategy) {
        this.id = id;
        this.customerName = customerName;
        this.weightKg = weightKg;
        this.distanceKm = distanceKm;
        this.deliveryStrategy = deliveryStrategy;
    }

    public void setDeliveryStrategy(DeliveryStrategy deliveryStrategy) {
        this.deliveryStrategy = deliveryStrategy;
    }

    public double getDeliveryCost() {
        return deliveryStrategy.calculateCost(weightKg, distanceKm);
    }

    public String getDeliveryName() {
        return deliveryStrategy.getName();
    }

    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public double getWeightKg() { return weightKg; }
    public double getDistanceKm() { return distanceKm; }
}

// ===== Вывод (отдельная ответственность, SRP) =====
class OrderPrinter {
    public void print(Order order) {
        System.out.println("Заказ №" + order.getId()
                + " | Клиент: " + order.getCustomerName()
                + " | Вес: " + order.getWeightKg() + " кг"
                + " | Расстояние: " + order.getDistanceKm() + " км");
        System.out.println("  Способ доставки: " + order.getDeliveryName());
        System.out.println("  Стоимость доставки: " + order.getDeliveryCost() + " тг");
        System.out.println();
    }
}

// ===== Main =====
public class Main {
    public static void main(String[] args) {
        OrderPrinter printer = new OrderPrinter();
        List<Order> orders = new ArrayList<>();

        // Сценарий 1: разные стратегии для разных заказов
        orders.add(new Order(1, "Айдар", 2.5, 10, new StandardDelivery()));
        orders.add(new Order(2, "Мадина", 1.0, 10, new ExpressDelivery()));
        orders.add(new Order(3, "Руслан", 5.0, 0, new PickupDelivery()));

        System.out.println("=== Сценарий 1: разные способы доставки ===");
        for (Order order : orders) {
            printer.print(order);
        }

        // Сценарий 2: смена стратегии во время выполнения
        System.out.println("=== Сценарий 2: смена способа доставки ===");
        Order order = orders.get(0);
        System.out.println("До смены:");
        printer.print(order);
        order.setDeliveryStrategy(new ExpressDelivery());
        System.out.println("После смены на экспресс:");
        printer.print(order);

        // Сценарий 3: новая стратегия без изменения Order (OCP)
        System.out.println("=== Сценарий 3: новый способ доставки (дрон) ===");
        Order droneOrder = new Order(4, "Алия", 1.5, 7, new DroneDelivery());
        printer.print(droneOrder);
    }
}