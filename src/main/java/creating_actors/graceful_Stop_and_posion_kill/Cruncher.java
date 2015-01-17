package creating_actors.graceful_Stop_and_posion_kill;

import akka.actor.UntypedActor;

public class Cruncher extends UntypedActor{
    @Override
    public void onReceive(Object message) throws Exception {

        System.out.println(message);

    }
}
