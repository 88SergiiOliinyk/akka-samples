package routing;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Created by Sergioli on 2/8/15.
 */
public class RouterLauncher {

    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create();

        ActorRef actor = system.actorOf(Props.create(Master.class));

        ActorRef roundRobin = system.actorOf(Props.create(RoundRobinRecipient.class));

        Work work = new Work("Hello Router");

        actor.tell(work, roundRobin);

    }
}
