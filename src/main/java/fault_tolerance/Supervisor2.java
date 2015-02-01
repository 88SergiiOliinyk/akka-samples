package fault_tolerance;

import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import static akka.actor.SupervisorStrategy.*;

import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.japi.Function;
import scala.Option;
import scala.concurrent.duration.Duration;

public class Supervisor2 extends UntypedActor {

    private static SupervisorStrategy strategy = new OneForOneStrategy(10,
            Duration.create("1 minute"),
            new Function<Throwable, Directive>() {
                @Override
                public Directive apply(Throwable t) {
                    if (t instanceof ArithmeticException) {
                        return resume();
                    } else if (t instanceof NullPointerException) {
                        return restart();
                    } else if (t instanceof IllegalArgumentException) {
                        return stop();
                    } else {
                        return escalate();
                    }
                }
            });

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }


    public void onReceive(Object o) {
        if (o instanceof Props) {
            getSender().tell(getContext().actorOf((Props) o), getSelf());
        } else {
            unhandled(o);
        }
    }

    @Override
    public void preRestart(Throwable cause, Option<Object> msg) {
        // do not kill all children, which is the default here
    }
}