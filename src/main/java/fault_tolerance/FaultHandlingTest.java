package fault_tolerance;

        import  akka.actor.ActorRef;
        import akka.actor.ActorSystem;
        import akka.actor.SupervisorStrategy;
        import static akka.actor.SupervisorStrategy.resume;
        import static akka.actor.SupervisorStrategy.restart;
        import static akka.actor.SupervisorStrategy.stop;
        import static akka.actor.SupervisorStrategy.escalate;
        import akka.actor.SupervisorStrategy.Directive;
        import akka.actor.OneForOneStrategy;
        import akka.actor.Props;
        import akka.actor.Terminated;
        import akka.actor.UntypedActor;
        import akka.testkit.JavaTestKit;
        import org.junit.AfterClass;
        import org.junit.BeforeClass;
        import org.junit.Test;
        import scala.collection.immutable.Seq;
        import scala.concurrent.Await;
        import static akka.pattern.Patterns.ask;
        import scala.concurrent.duration.Duration;
        import akka.testkit.TestProbe;

        import java.util.concurrent.TimeUnit;

public class FaultHandlingTest {
    static ActorSystem system;
    Duration timeout = Duration.create(5, TimeUnit.SECONDS);

    @BeforeClass
    public static void start() {
        system = ActorSystem.create("test");
    }

    @AfterClass
    public static void cleanup() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void mustEmploySupervisorStrategy() throws Exception {
       Props superprops = Props.create(Supervisor.class);
        ActorRef supervisor = system.actorOf(superprops, "supervisor");
        ActorRef child = (ActorRef) Await.result(ask(supervisor, Props.create(Child.class), 5000), timeout);
        child.tell(42, ActorRef.noSender());
        assert Await.result(ask(child, "get", 5000), timeout).equals(42);
        child.tell(new ArithmeticException(), ActorRef.noSender());
        assert Await.result(ask(child, "get", 5000), timeout).equals(42);

    }


    @Test
    public void childActorWillNotSurvive() throws Exception {
        Props superprops = Props.create(Supervisor.class);
        ActorRef supervisor = system.actorOf(superprops, "supervisor");
        ActorRef child = (ActorRef) Await.result(ask(supervisor, Props.create(Child.class), 5000), timeout);

        child.tell(new NullPointerException(), ActorRef.noSender());
        assert Await.result(ask(child, "get", 5000), timeout).equals(42);

    }

    @Test
    public void childWillBeStoppedBySupervisorTest() throws Exception {
        Props superprops = Props.create(Supervisor.class);
        ActorRef supervisor = system.actorOf(superprops, "supervisor");
        ActorRef child = (ActorRef) Await.result(ask(supervisor, Props.create(Child.class), 5000), timeout);

        final TestProbe probe = new TestProbe(system);
        probe.watch(child);
        child.tell(new IllegalArgumentException(), ActorRef.noSender());
        probe.expectMsg(Terminated.class);

    }

    @Test
    public void supervisorEscalatesException() throws Exception {
        Props superprops = Props.create(Supervisor.class);
        ActorRef supervisor = system.actorOf(superprops, "supervisor");
        ActorRef child = (ActorRef) Await.result(ask(supervisor, Props.create(Child.class), 5000), timeout);

        final TestProbe probe = new TestProbe(system);
        probe.watch(child);
        assert Await.result(ask(child, "get", 5000), timeout).equals(0);
        child.tell(new Exception(), ActorRef.noSender());
        probe.expectMsgClass(Terminated.class);

    }

    @Test
    public void supervisorsChildrenDoNotKill() throws Exception {
        Props superprops = Props.create(Supervisor2.class);
        ActorRef supervisor = system.actorOf(superprops);
        ActorRef child = (ActorRef) Await.result(ask(supervisor,
                Props.create(Child.class), 5000), timeout);
        child.tell(23, ActorRef.noSender());
        assert Await.result(ask(child, "get", 5000), timeout).equals(23);
        child.tell(new Exception(), ActorRef.noSender());
        assert Await.result(ask(child, "get", 5000), timeout).equals(0);
    }
}