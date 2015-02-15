package routing;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.*;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sergioli on 2/8/15.
 */
public class Master extends UntypedActor {

    Router router;

    {
        List<Routee>  routees = new ArrayList<Routee>();
        for(int i = 0; i < 5; i++){
            ActorRef r = getContext().actorOf(Props.create(WorkerForRouter.class));
            getContext().watch(r);
            routees.add(new ActorRefRoutee(r));
        }
        router = new Router(new RoundRobinRoutingLogic(), routees);
    }


    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof Work){
            System.out.println(message);
            router.route(message, getSender());
        }else if (message instanceof Terminated){
            router = router.removeRoutee(((Terminated)message).actor());
            ActorRef r = getContext().actorOf(Props.create(Work.class));
            getContext().watch(r);
            router = router.addRoutee(new ActorRefRoutee(r));
        }
    }
}
