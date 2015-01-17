package creating_actors.recieve_timeout;

import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;

public class MyReceiveTimeoutUntypedActor extends UntypedActor {

    public MyReceiveTimeoutUntypedActor() {
        // To set an initial delay
        getContext().setReceiveTimeout(Duration.create("30 seconds"));
    }

    public void onReceive(Object message) {
        if (message.equals("Hello")) {
            // To set in a response to a message
            getContext().setReceiveTimeout(Duration.create("1 second"));
        } else if (message instanceof ReceiveTimeout) {
            // To turn it off
            System.out.println("Timed out");
            getContext().setReceiveTimeout(Duration.Undefined());
        } else {
            unhandled(message);
        }
    }
}