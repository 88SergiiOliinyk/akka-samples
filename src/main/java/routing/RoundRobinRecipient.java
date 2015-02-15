package routing;

import akka.actor.UntypedActor;

/**
 * Created by Sergioli on 2/8/15.
 */
public class RoundRobinRecipient extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("Have got from round robing " + message);
    }
}
