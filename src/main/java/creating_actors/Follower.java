package creating_actors;

import akka.actor.*;

public class Follower extends UntypedActor {

    final String identifyId = "1";

    {
        ActorSelection selection =
                getContext().actorSelection("/user/another");
        selection.tell(new Identify(identifyId), getSelf());
    }

    ActorRef another;

    final ActorRef probe;

    public Follower() {
        this.probe = getSelf();
    }

    @Override
    public void onReceive(Object message) {

        if (message instanceof ActorIdentity) {
            ActorIdentity identity = (ActorIdentity) message;
            System.out.println(message);
            if (identity.correlationId().equals(identifyId)) {
                ActorRef ref = identity.getRef();
                if (ref == null) {
                    System.out.println(ref);
                    getContext().stop(getSelf());
                } else {
                    another = ref;
                    getContext().watch(another);
                    probe.tell(ref, getSelf());
                }
            }
        } else if (message instanceof Terminated) {
            System.out.println(message);
            final Terminated t = (Terminated) message;
            if (t.getActor().equals(another)) {
                getContext().stop(getSelf());
            }
        } else {
            unhandled(message);
        }
    }

    public static void main(String[] args) {
        final ActorSystem actorSystem = ActorSystem.create("ServerEvents");
        final ActorRef follower = actorSystem.actorOf(Props.create(Follower.class), "another");

    }
}