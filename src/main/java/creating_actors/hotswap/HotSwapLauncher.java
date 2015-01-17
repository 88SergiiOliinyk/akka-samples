package creating_actors.hotswap;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class HotSwapLauncher {

    public static void main(String[] args) throws Exception {

        final ActorSystem actorSystem = ActorSystem.create();
        final ActorRef hotswaplauncher = actorSystem.actorOf(Props.create(HotSwapActor.class), "hotswaplauncher");

        hotswaplauncher.tell("foo", ActorRef.noSender());
        // the actor has been stopped

    }
}
