package creating_actors.graceful_Stop_and_posion_kill;

import akka.actor.*;
import akka.japi.Procedure;

public class Manager extends UntypedActor {

    public static final String SHUTDOWN = "shutdown";

    ActorRef worker = getContext().watch(getContext().actorOf(
            Props.create(Cruncher.class), "worker"));

    public void onReceive(Object message) {
        if (message.equals("job")) {
            worker.tell("crunch", getSelf());
        } else if (message.equals(SHUTDOWN)) {
            System.out.println("Shutting down");
            worker.tell(PoisonPill.getInstance(), getSelf());
            getContext().become(shuttingDown);
        }
    }

    Procedure<Object> shuttingDown = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            if (message.equals("job")) {
                getSender().tell("service unavailable, shutting down", getSelf());
            } else if (message instanceof Terminated) {
                System.out.println("Stopping the actor");
                getContext().stop(getSelf());
            }
        }
    };
}