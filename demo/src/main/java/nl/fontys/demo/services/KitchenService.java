package nl.fontys.demo.services;

import java.util.ArrayList;
import java.util.List;
import net.engio.mbassy.listener.Handler;
import nl.fontys.demo.events.Event;
import nl.fontys.demo.events.EventBus;
import nl.fontys.demo.events.EventType;

public class KitchenService {

    // After 3 meals a cooked, inform the order service
    public final int SEND_OUT_GAP = 3;

    private List<String> cookedMeals = new ArrayList<String>();

    private EventBus bus = EventBus.getInstance();

    public KitchenService() {
        bus.subscribe(this);
    }

    @Handler
    public void onEvent(Event event) {
        if (event.isTypeOf(EventType.KITCHEN_SALARY_SENT)) {
            receiveSalary((Float)event.getArgument(0));
        } else if (event.isTypeOf(EventType.MEAL_ORDERED)) {
            cookMeal((String)event.getArgument(0));
        }
    }

    public void receiveSalary(float dollars) {
        System.out.println("Kitchen service received " + dollars + " salary.");
    }

    public void cookMeal(String meal) {
        System.out.println("Cooking meal " + meal);
        cookedMeals.add(meal);
        // Send out all cooked meals so far to the order service
        if (cookedMeals.size() == SEND_OUT_GAP) {
            for (String cookedMeal : cookedMeals) {
                bus.publish(new Event(EventType.MEAL_COOKED, cookedMeal));
            }
            cookedMeals.clear();
        }
    }
}
