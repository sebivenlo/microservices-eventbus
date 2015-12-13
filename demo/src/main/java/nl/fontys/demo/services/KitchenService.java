package nl.fontys.demo.services;

import java.util.ArrayList;
import java.util.List;

public class KitchenService {

    // After 3 meals a cooked, inform the order service
    public final int SEND_OUT_GAP = 3;

    private OrderService orderService;

    private List<String> cookedMeals = new ArrayList<String>();

    public void setup(OrderService orderService) {
        this.orderService = orderService;
    }

    public void receiveSalary(float dollars) {
        System.out.println("Kitchen service received " + dollars + " sallary.");
    }

    public void cookMeal(String meal) {
        System.out.println("Cooking meal " + meal);
        cookedMeals.add(meal);
        // Send out all cooked meals so far to the order service
        if (cookedMeals.size() == SEND_OUT_GAP) {
            for (String cookedMeal : cookedMeals) {
                orderService.receiveMeal(cookedMeal);
            }
            cookedMeals.clear();
        }
    }
}
