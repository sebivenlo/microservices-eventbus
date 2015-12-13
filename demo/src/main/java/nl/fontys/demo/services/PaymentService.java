/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.fontys.demo.services;

import nl.fontys.demo.events.Event;
import nl.fontys.demo.events.EventBus;
import nl.fontys.demo.events.EventType;

public class PaymentService {
    
    private EventBus bus = EventBus.getInstance();
    
    public PaymentService() {
        bus.subscribe(this);
    }

    public void payMeal(String meal, float ammount) {
        System.out.println("Payment received for meal: " + meal + " for: "+ ammount +"$");
    }

    public void paySalary(float dollars) {
        bus.publish(new Event(EventType.KITCHEN_SALARY_SENT, dollars));
        bus.publish(new Event(EventType.ORDER_SALARY_SENT, dollars));
    }
}
