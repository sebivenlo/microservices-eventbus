---
title:  "Dependency problems"
---
In the previous chapter you may have noticed that there is a real mess in terms of dependencies. Each service needs to know other services to establish proper communication. This problem gets really serious (and dangerous) if we want to and even more services such as customer support or finance management or ingredient ordering. To resolve the issue we want to introduce to you a nice and elegant way to deal with those dependencies.

<div class="hint info"><i class="fa fa-info-circle"></i>You can never eradicate dependencies completely but you can shift them to a place where they can be managed better.</div>

### Events

A great approach are event buses. Imagine some kind of manager person who is responsible for telling everyone what happened independently. We use for that purpose [Mbassador](https://github.com/bennidi/mbassador). The demo project has already a maven dependency so you do not need to bother about how to download it.

Let us first create our own event class. Events are used as communication "message" to tell others what happened before.

<div class="hint warning"><i class="fa fa-exclamation-triangle"></i> Never create events to tell that something should happen. Always fire events AFTER something happened.</div>

Create a new package, called ```nl.fontys.demo.events``` and add a new class, called ```Event```:

{% highlight java %}
package nl.fontys.demo.events;

public class Event {

    private String type;

    private Object[] args;

    public Event(String type, Object ... args) {
        this.args = args;
        this.type = type;
    }

    public Object getArgument(int index) {
        if (index >= 0 && index < args.length) {
            return args[index];
        } else {
            return null;
        }
    }

    public boolean isTypeOf(String type) {
        return this.type.equals(type);
    }
}
{% endhighlight %}

Each event has a so called ```type``` as well as a list of arguments (optional). An argument is an object (information) which should be told to others.

### The event bus

To use such events we need to distribute them. The distribution of events is done by a so called **Event Bus** handler. *Mbassador* itself provides an event bus implementation. Since we want
to ease the process we make it [Singleton](https://en.wikipedia.org/wiki/Singleton_pattern) and wrap it into a ```EventBus``` class:
{% highlight java %}
package nl.fontys.demo.events;

import net.engio.mbassy.bus.MBassador;

public final class EventBus {

    private static final EventBus INSTANCE = new EventBus();

    private MBassador<Event> mbassador;

    private EventBus() {
        mbassador = new MBassador<Event>();
    }

    public static EventBus getInstance() {
        return INSTANCE;
    }

    public void subscribe(Object object) {
        mbassador.subscribe(object);
    }

    public void unsubscribe(Object object) {
        mbassador.unsubscribe(object);
    }

    public void publish(Event event) {
        mbassador.publish(event);
    }
}
{% endhighlight %}

By calling ```subscribe``` we are able to subscribe (listen) to changes which are sent through the bus. To publish a new event we can call the ```publish``` method. In our case the publication of the event is synchronous and single-threaded. *Mbassador* provides also options for asynchronous event distribution, which is very interesting in terms of high performance applications.

### Event types

By just having events it is hard to see which event holds which kind of information. Each event type may come with an individual size of arguments. To solve the issue we need event types. In our case event types are just of type ```java.lang.String```. Create a new class, called ```EventType``` and add the following:
{% highlight java %}
package nl.fontys.demo.events;

public class EventType {
    public static final String MEAL_COOKED = "meal_cooked";
    public static final String MEAL_DELIVERED = "meal_delivered";
    public static final String MEAL_ORDERED = "meal_ordered";
    public static final String KITCHEN_SALARY_SENT = "kitchen_salary_sent";
    public static final String ORDER_SALARY_SENT = "order_salary_sent";
}
{% endhighlight %}
As you can see for each event which may occur there is an own type:

* ```MEAL_COOKED```: is called after a meal has been cooked by the kitchen AND is ready to get fetched by a waiter
* ```MEAL_DELIVERED```: is called after a meal has been delivered to a customer by the order service
* ```MEAL_ORDERED```: is called after a meal has been ordered
* ```KITCHEN_SALARY_SENT```: is called when the payment service has sent the salary for the kitchen staff
* ```ORDER_SALARY_SENT```: is called when the payment service has sent the salary for the waiter staff

Now it is up to us to rewrite our services to take advantage of the power of event buses!

### Rewrite the order service

First of all, we remove all dependencies (via setup method) from our services. Then we fetch the singleton instance of the event bus, subscribe to it and listen to event calls by creating a method annotated via ```@Handler```:
{% highlight java %}
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
        System.out.println("Order service received " + dollars + " salary.");
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
{% endhighlight %}

As you may notice already the service does not have any dependency of other services anymore!

### Rewrite the kitchen service

Let us do the same for the kitchen service:
{% highlight java %}
package nl.fontys.demo.services;

import net.engio.mbassy.listener.Handler;
import nl.fontys.demo.events.Event;
import nl.fontys.demo.events.EventBus;
import nl.fontys.demo.events.EventType;

public class PaymentService {

    private EventBus bus = EventBus.getInstance();

    public PaymentService() {
        bus.subscribe(this);
    }

    @Handler
    public void onEvent(Event event) {
        if (event.isTypeOf(EventType.MEAL_DELIVERED)) {
            payMeal((String)event.getArgument(0), 1);
        }
    }

    public void payMeal(String meal, float amount) {
        System.out.println("Payment received for meal: " + meal + " for: "+ amount +"$");
    }

    public void paySalary(float dollars) {
        bus.publish(new Event(EventType.KITCHEN_SALARY_SENT, dollars));
        bus.publish(new Event(EventType.ORDER_SALARY_SENT, dollars));
    }
}
{% endhighlight %}

### Rewrite the payment service

And for the payment service:
{% highlight java %}
package nl.fontys.demo.services;

import net.engio.mbassy.listener.Handler;
import nl.fontys.demo.events.Event;
import nl.fontys.demo.events.EventBus;
import nl.fontys.demo.events.EventType;

public class PaymentService {

    private EventBus bus = EventBus.getInstance();

    public PaymentService() {
        bus.subscribe(this);
    }

    @Handler
    public void onEvent(Event event) {
        if (event.isTypeOf(EventType.MEAL_DELIVERED)) {
            payMeal((String)event.getArgument(0), 1);
        }
    }

    public void payMeal(String meal, float amount) {
        System.out.println("Payment received for meal: " + meal + " for: "+ amount +"$");
    }

    public void paySalary(float dollars) {
        bus.publish(new Event(EventType.KITCHEN_SALARY_SENT, dollars));
        bus.publish(new Event(EventType.ORDER_SALARY_SENT, dollars));
    }
}
{% endhighlight %}

### Changing the base Application

Now all dependencies are gone between the services. This means that we can create as many services as we want and they will work completely independently without knowing about the others. As you may have noticed already: we only fire events after something has happened. We never react with an event. This is very important to avoid string coupling and create responsibility at the wrong place.

Let us move on with the application code:
{% highlight java %}
package nl.fontys.demo;

import nl.fontys.demo.services.KitchenService;
import nl.fontys.demo.services.OrderService;
import nl.fontys.demo.services.PaymentService;

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
{% endhighlight %}


The result is the same! From the API side of view those services are far easier to use now. Of course this example is very rough and simple, but imagine an architecture with hundreds of services. This way avoids dependency hassle and creates a clean structure.

When you run the Demo you should get the following output:
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

Looks the same as before ? Congratulations you did everything right!
