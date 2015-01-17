package event_bus;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class ActorSys {

    public static void main(String[] args) {
        final ActorSystem actorSystem = ActorSystem.create("ServerEvents");
        final ActorRef firstActor = actorSystem.actorOf(Props.create(FirstActor.class));
        final ActorRef secondActor = actorSystem.actorOf(Props.create(SecondActor.class));

        actorSystem.eventStream().subscribe(firstActor, MsgEnvelope.class);
        actorSystem.eventStream().subscribe(secondActor, MsgEnvelope.class);

        actorSystem.eventStream().publish(new MsgEnvelope("greetings", "hello"));
        actorSystem.eventStream().publish(new MsgEnvelope("greetings2", "hello2"));
        actorSystem.eventStream().publish(new MsgEnvelope("greetings3", "hello3"));
    }
}
