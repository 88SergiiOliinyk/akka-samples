package creating_actors.stash_unstash;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorWithStash;
import akka.japi.Procedure;
import creating_actors.recieve_timeout.MyReceiveTimeoutUntypedActor;

public class ActorWithProtocol extends UntypedActorWithStash {
    public void onReceive(Object msg) {
        if (msg.equals("open")) {
            System.out.println("open message");
            unstashAll();
            System.out.println("unstashing");
            getContext().become(new Procedure<Object>() {
                public void apply(Object msg) throws Exception {
                    if (msg.equals("write")) {
                        System.out.println("writing message");
                    } else if (msg.equals("close")) {
                        System.out.println("close message");
                        unstashAll();
                        getContext().unbecome();
                    } else {
                        stash();
                    }
                }
            }, false); // add behavior on top instead of replacing
        } else {
            stash();
        }
    }

    public static void main(String[] args) {
        final ActorSystem actorSystem = ActorSystem.create();
        final ActorRef actorwithprotocol = actorSystem.actorOf(Props.create(ActorWithProtocol.class), "actorwithprotocol");

        actorwithprotocol.tell("write", actorwithprotocol);
        actorwithprotocol.tell("close", actorwithprotocol);
        actorwithprotocol.tell("open", actorwithprotocol);
        // the actor has been stopped
    }
}