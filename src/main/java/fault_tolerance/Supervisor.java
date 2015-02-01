package fault_tolerance;

import akka.actor.OneForOneStrategy;


import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.japi.Function;
import scala.concurrent.duration.Duration;
import static akka.actor.SupervisorStrategy.*;

/**
 * Created by Sergioli on 2/1/15.
 */
public class Supervisor extends UntypedActor {

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create("1 minute"),
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
    public SupervisorStrategy supervisorStrategy(){
        return strategy;
    }

    public void onReceive(Object o) {
        if (o instanceof Props) {
            getSender().tell(getContext().actorOf((Props) o), getSelf());
        } else {
            unhandled(o);
        }
    }


}
