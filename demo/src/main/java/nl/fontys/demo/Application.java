package nl.fontys.demo;

import nl.fontys.demo.services.KitchenService;
import nl.fontys.demo.services.OrderService;
import nl.fontys.demo.services.PaymentService;

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
        PaymentService paymentService = new PaymentService();
        // Add some orders
        orderService.addOrder("Soup");
        orderService.addOrder("Bread");
        orderService.addOrder("Steak");
        // Send orders to the kitchen
        orderService.sendOrders();
        // Deliver all cooked meals
        orderService.deliverMeals();
        // Let us pay our employees
        paymentService.paySalary(500);
    }
}
