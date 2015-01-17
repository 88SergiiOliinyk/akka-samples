package creating_actors.recieve_timeout;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class TimeOutLauncher {
    public static void main(String[] args) throws Exception {

        final ActorSystem actorSystem = ActorSystem.create();
        final ActorRef manager = actorSystem.actorOf(Props.create(MyReceiveTimeoutUntypedActor.class), "manager");

          manager.tell("Hello", manager);
        // the actor has been stopped
    }
}
