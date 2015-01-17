package creating_actors.graceful_Stop_and_posion_kill;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class LauncherStopingGracefull {

    public static void main(String[] args) throws Exception {

        final ActorSystem actorSystem = ActorSystem.create();
        final ActorRef manager = actorSystem.actorOf(Props.create(Manager.class), "manager");

            Future<Boolean> stopped =
                    Patterns.gracefulStop(manager, Duration.create(5, TimeUnit.SECONDS), Manager.SHUTDOWN);
            Await.result(stopped, Duration.create(6, TimeUnit.SECONDS));
            // the actor has been stopped

    }
}
