/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.fontys.demo.services;

public class PaymentService {

    private OrderService orderService;

    private KitchenService kitchenService;

    public void setup(OrderService orderService, KitchenService kitchenService) {
        this.orderService = orderService;
        this.kitchenService = kitchenService;
    }
    public void payMeal(String meal, float ammount) {
        System.out.println("Payment received for meal: " + meal + " for: "+ ammount +"$");
    }

    public void paySalary(float dollars) {
        System.out.println("Paying Kitchen " + dollars + " sallary.");
        kitchenService.receiveSalary(dollars);
        System.out.println("Paying Waiter " + dollars + " sallary.");
        orderService.receiveSalary(dollars);
    }
}
