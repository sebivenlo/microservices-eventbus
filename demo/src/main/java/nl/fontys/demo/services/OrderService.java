package nl.fontys.demo.services;

import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private List<String> orders = new ArrayList<String>();
    
    private List<String> meals = new ArrayList<String>();

    private KitchenService kitchenService;
    
    private PaymentService paymentService;
    
    public void setup(KitchenService kitchenService, PaymentService paymentService) {
        this.kitchenService = kitchenService;
        this.paymentService = paymentService;
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
                paymentService.payMeal(meal, 1);
            }
            meals.clear();
        } else {
            System.out.println("Could not deliver any meals. No meals available!");
        }
    }

    public void sendOrders() {
        for (String order : orders) {
            kitchenService.cookMeal(order);
        }
        orders.clear();
    }
}
