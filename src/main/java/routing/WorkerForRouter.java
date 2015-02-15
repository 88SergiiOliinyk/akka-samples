package routing;

import akka.actor.UntypedActor;

/**
 * Created by Sergioli on 2/8/15.
 */
public class WorkerForRouter extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof Work){
            System.out.println(message + " " + getSelf());
            getSender().tell("Hello from Worker", getSelf());
        }
        else System.out.println("It's not the Work instance of: " + message);
    }
}
