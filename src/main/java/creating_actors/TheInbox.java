package creating_actors;


import akka.actor.*;
import event_bus.FirstActor;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class TheInbox {

    public static void main(String[] args) {

        final ActorSystem actorSystem = ActorSystem.create("ServerEvents");
        final ActorRef firstActor = actorSystem.actorOf(Props.create(FirstActor.class));
        final Inbox inbox = Inbox.create(actorSystem);

//        inbox.send(firstActor, "hello");
//
//        System.out.println(inbox.receive(Duration.create(1, TimeUnit.SECONDS)).equals("world"));

        inbox.watch(firstActor);

        firstActor.tell(PoisonPill.getInstance(), ActorRef.noSender());

        System.out.println(inbox.receive(Duration.create(1, TimeUnit.SECONDS)) instanceof Terminated);

        System.out.println(firstActor.isTerminated());
    }


}
