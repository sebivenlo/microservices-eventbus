---
title: A case study - Restaurant
---
Imagine for a second you are a restaurant owner and you want to improve your processes by applying micro services. Such restaurant could be managed in the following way:

<img src="{{ "/img/restaurant-setup.png" | prepend: site.baseurl }}" alt="restaurant"/>

The **order service** receives orders and informs the **kitchen service** to cook certain meals. After a meal has been cooked the **order service** (a waiter) should get informed so the meal can get out to the actual customer. After a customer has finished his meal, the **payment service** takes over to receive the money from the customer. Also the payment service pays the employees such as the kitchen and the waiters.

### The kitchen service

If you haven't [downloaded the initial demo project](https://github.com/sebivenlo/microservices-eventbus/archive/5aeaa8b0b936b6778d9b337cf7e577504354d417.zip) yet, do it now. In the package ```nl.fontys.demo``` create a new package, called ```services```. In this package, create a new Java class, called ```KitchenService```:

{% highlight java %}
package nl.fontys.demo.services;

public class KitchenService {

    public void receiveSalary(float dollars) {
        System.out.println("Kitchen service received " + dollars + " salary.");
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

    public setup(KitchenService kitchenService) {
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

    public setup(OrderService orderService) {
        this.orderService = orderService;
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
                orderService.receiveMeal(cookedMeal);
            }
            cookedMeals.clear();
        }
    }
}
{% endhighlight %}
Now the ```KitchenService``` class also depends on the ```OrderService```

The last step to complete our restaurant is to add the ```PaymentService``` to everything. The Payment service needs to get informed every time an order is served. After the waiter served the order, the restaurant gets payed and the ```PaymentService``` pays our employees.

Lets first implement the ```PaymentService```:

{% highlight java %}
package nl.fontys.demo.services;

public class PaymentService {

    private OrderService orderService;

    private KitchenSevice kitchenService;

    public void setup(OrderService orderService, KitchenSevice kitchenService) {
        this.orderService = orderService;
        this.kitchenService = kitchenService;
    }
    public void payMeal(String meal, float ammount) {
        System.out.println("Payment received for meal: " + meal + " for: "+ amount +"$");
        paySalary(ammount);
    }

    private void paySalary(float dollars) {
        System.out.println("Paying Kitchen " + dollars + " salary.");
        kitchenService.receiveSalary(dollars);
        System.out.println("Paying Waiter " + dollars + " salary.");
        orderService.receiveSalary(dollars);
    }
}
{% endhighlight %}

Now every time an meal is delivered and payed, the kitchen and waiter get payed. This is a very simple implementation for demo purposes and doesn't reflect a real business workflow.

### Wrap everything together

After we setup all services it is time to create our first application. This simple Java application will setup all services and process some orders. Create a new class, called ```Application``` and create a main method:
{% highlight java %}
public class Application {

    public static void main(String[] args) {
        // Setup all services
        OrderService orderService = new OrderService();
        KitchenService kitchenService = new KitchenService();
        PaymentService paymentService = new PaymentService();
        orderService.setup(kitchenService, paymentService);
        kitchenService.setup(orderService);
        paymentService.setup(orderService, kitchenService);
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
{% endhighlight %}
When you run it now you should see :
{% highlight console %}
Cooking meal Soup
Cooking meal Bread
Cooking meal Steak
Deliver meal to customer: Soup
Payment received for meal: Soup for: 1.0$
Deliver meal to customer: Bread
Payment received for meal: Bread for: 1.0$
Deliver meal to customer: Steak
Payment received for meal: Steak for: 1.0$
Kitchen service received 500.0 salary.
Order service received 500.0 salary.
{% endhighlight %}


This looks good, doesn't it? Well, in fact here are some issues already with this example implementation above:

* a lot of dependencies already: only 3 services and all must be setup correctly
* what when adding new services to the application? We would need to rewrite all services to add compatibility
* the example above requires initialization ordering: when creating objects too late, ```NullPointerException``` may occur

How to solve those issues? Take a look into the next chapter <a href="{{ "/tutorials/03-dependencies" | prepend: site.baseurl }}">here</a>!
