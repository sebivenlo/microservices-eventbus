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

To use such events we need to distribute them. The distribution of events is done by a so called **Event Bus** handler. MBassador itself provides an event bus implementation. Since we want
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
