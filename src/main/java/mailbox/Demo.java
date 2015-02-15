package mailbox;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.ConfigFactory;

/**
 * Created by Sergioli on 2/1/15.
 */
public class Demo extends UntypedActor{

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public void onReceive(Object message) {
        log.info(message.toString());
    }

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create();

        ActorRef myActor = actorSystem.actorOf(Props.create(Demo.class).withDispatcher("prio-dispatcher"));

        for (Object msg : new Object[] { "lowpriority", "lowpriority",
                "highpriority", "pigdog", "pigdog2", "pigdog3", "highpriority",
                PoisonPill.getInstance() }) {
            myActor.tell(msg, myActor);
        }

    }
}
