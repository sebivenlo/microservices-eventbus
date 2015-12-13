package nl.fontys.demo;

import nl.fontys.demo.services.KitchenService;
import nl.fontys.demo.services.OrderService;

/**
 * Application to run the demo
 *
 * @author Miguel Gonzalez
 * @author Jan Kerkenhoff
 */
public class Application {

    public static void main(String[] args) {        
        // Setup all services
        OrderService orderService = new OrderService();
        KitchenService kitchenService = new KitchenService();
        orderService.setup(kitchenService);
        kitchenService.setup(orderService);
        // Add some orders
        orderService.addOrder("Soup");
        orderService.addOrder("Bread");
        orderService.addOrder("Steak");
        // Send orders to the kitchen
        orderService.sendOrders();
        // Deliver all cooked meals
        orderService.deliverMeals();
    }
}
