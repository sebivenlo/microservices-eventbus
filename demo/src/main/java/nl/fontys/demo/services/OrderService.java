package nl.fontys.demo.services;

import java.util.ArrayList;
import java.util.List;
import net.engio.mbassy.listener.Handler;
import nl.fontys.demo.events.Event;
import nl.fontys.demo.events.EventBus;
import nl.fontys.demo.events.EventType;

public class OrderService {

    private List<String> orders = new ArrayList<String>();
    
    private List<String> meals = new ArrayList<String>();
    
    private EventBus bus = EventBus.getInstance();
    
    public OrderService() {
        bus.subscribe(this);
    }
    
    @Handler
    public void onEvent(Event event) {
        if (event.isTypeOf(EventType.ORDER_SALARY_SENT)) {
            receiveSalary((Float)event.getArgument(0));
        } else if (event.isTypeOf(EventType.MEAL_COOKED)) {
            receiveMeal((String)event.getArgument(0));
        }
    }

    public void addOrder(String orderedMeal) {
        orders.add(orderedMeal);
    }
    
    public void receiveMeal(String meal) {
        meals.add(meal);
    }
    
    public void receiveSalary(float dollars) {
        System.out.println("Order service received " + dollars + " sallary.");
    }
    
    public void deliverMeals() {
        if (meals.size() > 0) {
            for (String meal : meals) {
                System.out.println("Deliver meal to customer: " + meal);
                bus.publish(new Event(EventType.MEAL_DELIVERED, meal));
            }
            meals.clear();
        } else {
            System.out.println("Could not deliver any meals. No meals available!");
        }
    }

    public void sendOrders() {
        for (String order : orders) {
            bus.publish(new Event(EventType.MEAL_ORDERED, order));
        }
        orders.clear();
    }
}
