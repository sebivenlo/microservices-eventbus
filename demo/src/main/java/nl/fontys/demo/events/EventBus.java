package nl.fontys.demo.events;

import net.engio.mbassy.bus.MBassador;

public final class EventBus {
    
    private static final EventBus INSTANCE = new EventBus();
    
    private MBassador mbassador;
    
    private EventBus() {
        /* private instance */
        mbassador = new MBassador();
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
    
    
}
