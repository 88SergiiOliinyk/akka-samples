package event_bus;

public class MsgEnvelope {
    public final String topic;
    public final Object payload;

    public MsgEnvelope(String topic, Object payload) {
        this.topic = topic;
        this.payload = payload;
    }
}
