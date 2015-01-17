package event_bus;

import akka.actor.UntypedActor;

public class FirstActor extends UntypedActor {

    @Override
    public void onReceive(final Object message) {
        System.out.println("Event: " + message  + " thread: " + Thread.currentThread().getName() + " First actor");
    }
}