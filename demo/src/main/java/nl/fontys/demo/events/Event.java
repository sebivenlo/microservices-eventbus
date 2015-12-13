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
