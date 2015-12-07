---
title: A case study - Restaurant
---
Imagine for a second you are a restaurant owner and you want to improve your processes by applying micro services. Such restaurant could be managed in the following way:

![restaurant-setup](/img/restaurant-setup.png)

The **order service** receives orders and informs the **kitchen service** to cook certain meals. After a meal has been cooked the **order service** (a waiter) should get informed so the meal can get out to the actual customer. After a customer has finished his meal, the **payment service** takes over to receive the money from the customer. Also the payment service pays the employees such as the kitchen and the waiters.

### The kitchen service

If you haven't [downloaded the initial demo project](https://github.com/sebivenlo/microservices-eventbus/archive/demo.zip) yet, do it now. In the package ```nl.fontys.demo``` create a new package, called ```services```. In this package, create a new Java class, called ```KitchenService```:

{% highlight java %}
package nl.fontys.demo.services;

public class KitchenService {

    public void receiveSalary(float dollars) {
        System.out.println("Kitchen service received " + dollars + " sallary.");
    }

    public void cookMeal(String meal) {
        System.out.println("Cooking meal " + meal);
    }
}
{% endhighlight %}

The kitchen service has to methods: one to receive salary of a certain amount and one method to cook a given meal.

### The order service

Create now another class in the ```nl.fontys.demo.services``` package, called ```OrderService```:

{% highlight java %}
package nl.fontys.demo.services;

import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private List<String> orders = new ArrayList<String>();

    private List<String> meals = new ArrayList<String>();

    private KitchenService kitchenService;

    public OrderService(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    public void addOrder(String orderedMeal) {
        orders.add(orderedMeal);
    }

    public void receiveMeal(String meal) {
        meals.add(meal);
    }

    public void deliverMeals() {
        if (meals.size() > 0) {
            for (String meal : meals) {
                System.out.println("Deliver meal to customer: " + meal);
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
{% endhighlight %}
This service is a little bit more complex. It needs a kitchen service to inform it when needed. Also it is able to receive multiple orders and send them all at once to the kitchen. It is also able to receive cooked meals to bring them to the customer. Now we have a first problem: due to a dependency between ```KitchenService``` and ```OrderService``` the kitchen service needs to inform the order service when a meal is ready. Let us tweak the ```KitchenService``` code a little bit:
{% highlight java %}
package nl.fontys.demo.services;

import java.util.ArrayList;
import java.util.List;

public class KitchenService {

    // After 3 meals a cooked, inform the order service
    public final int SEND_OUT_GAP = 3;

    private OrderService orderService;

    private List<String> cookedMeals = new ArrayList<String>();

    public KitchenService(OrderService orderService) {
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
{% endhighlight %}
Now the ```KitchenService``` class also depends on the ```OrderService```

The last step to complete our restaurant is to add the ```PaymentService``` to everything. The Payment service needs to get informed everytime an order is served. After the waiter served the order, the restaurant gets payed and the ```PaymentService``` pays our empolyees.

Lets first implement the ```PaymentService```:

{% highlight java %}
package nl.fontys.demo.services;

public class PaymentService {

    private OrderService orderService;

    private KitchenSevice kitchenService;

    public PaymentService(OrderService orderService, KitchenSevice kitchenService) {
        this.orderService = orderService;
        this.kitchenService = kitchenService;
    }
    public void payMeal(String meal, float ammount) {
        System.out.println("Payment received for meal: " + meal + " for: "+ ammount +"$");
        paySalary(ammount);
    }

    private void paySalary(float dollars) {
        System.out.println("Paying Kitchen " + dollars + " sallary.");
        kitchenService.receiveSalary(dollars);
        System.out.println("Paying Waiter " + dollars + " sallary.");
        orderService.receiveSalary(dollars);
    }
}
{% endhighlight %}

Now everytime an meal is delivered and payed, the kitchen and waiter get payed. This is a very simple implementation for demo purposes and doesn't reflect a real business workflow.
