package typed_actors;

import akka.actor.TypedActor;
import akka.actor.*;
import akka.japi.*;
import akka.dispatch.Futures;

import org.junit.Before;
import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import akka.routing.RoundRobinGroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TypedActorDocTest {

    ActorSystem system;
    Object someReference;
    @Before
    public void startUp() {
         someReference = null;
         system = ActorSystem.create();
    }

    public interface Squarer {
        void squareDontCare(int i); //fire-forget

        Future<Integer> square(int i); //non-blocking send-request-reply

        Option<Integer> squareNowPlease(int i);//blocking send-request-reply

        int squareNow(int i); //blocking send-request-reply

        public Squarer createChildSquarer();
    }

    static
    class SquarerImpl implements Squarer {
        private String name;

        public SquarerImpl() {
            this.name = "default";
        }

        public SquarerImpl(String name) {
            this.name = name;
        }


        public void squareDontCare(int i) {
            int sq = i * i; //Nobody cares :(
        }

        public Future<Integer> square(int i) {
            return Futures.successful(i * i);
        }

        public Option<Integer> squareNowPlease(int i) {
            return Option.some(i * i);
        }

        public int squareNow(int i) {
            return i * i;
        }

        public Squarer createChildSquarer(){
            Squarer childSquarer = TypedActor.get(TypedActor.context()).typedActorOf(
                    new TypedProps<SquarerImpl>(Squarer.class, SquarerImpl.class));
            return childSquarer;
        }
    }

    @Test
    public void mustGetTheTypedActorExtension() {


            //Returns the Typed Actor Extension
            TypedActorExtension extension =
                    TypedActor.get(system); //system is an instance of ActorSystem

            //Returns whether the reference is a Typed Actor Proxy or not
            TypedActor.get(system).isTypedActor(someReference);

            //Returns the backing Akka Actor behind an external Typed Actor Proxy
            TypedActor.get(system).getActorRefFor(someReference);

            //Returns the current ActorContext,
            // method only valid within methods of a TypedActor implementation
            ActorContext context = TypedActor.context();

            //Returns the external proxy of the current Typed Actor,
            // method only valid within methods of a TypedActor implementation
            Squarer sq = TypedActor.self();

            //Returns a contextual instance of the Typed Actor Extension
            //this means that if you create other Typed Actors with this,
            //they will become children to the current Typed Actor.
            TypedActor.get(TypedActor.context());

    }
    @Test public void createATypedActor() {
        try {
            Squarer mySquarer = TypedActor.get(system).typedActorOf(
                    new TypedProps<SquarerImpl>(Squarer.class,SquarerImpl.class));

            Squarer otherSquarer =
                    TypedActor.get(system).typedActorOf(
                            new TypedProps<SquarerImpl>(Squarer.class,
                                    new Creator<SquarerImpl>() {
                                        public SquarerImpl create() { return new SquarerImpl("foo"); }
                                    }),
                            "name");

            mySquarer.squareDontCare(10);

            Future<Integer> fSquare = mySquarer.square(10); //A Future[Int]

            Option<Integer> oSquare = mySquarer.squareNowPlease(10); //Option[Int]

            int iSquare = mySquarer.squareNow(10); //Int

            assertEquals(100, Await.result(fSquare,
                    Duration.create(3, TimeUnit.SECONDS)).intValue());

            assertEquals(100, oSquare.get().intValue());

            assertEquals(100, iSquare);

            TypedActor.get(system).stop(mySquarer);

            TypedActor.get(system).poisonPill(otherSquarer);
        } catch(Exception e) {
            //Ignore
        }
    }

    @Test public void createHierarchies() {
        try {
            Squarer childSquarer =
                    TypedActor.get(TypedActor.context()).
                            typedActorOf(
                                    new TypedProps<SquarerImpl>(Squarer.class, SquarerImpl.class)
                            );
            //Use "childSquarer" as a Squarer
        } catch (Exception e) {
            //dun care
        }
    }

    @Test
    public void proxyAnyActorRef() {
        try {
            final ActorRef actorRefToRemoteActor = system.deadLetters();
            Squarer typedActor =
                    TypedActor.get(system).
                            typedActorOf(
                                    new TypedProps<Squarer>(Squarer.class),
                                    actorRefToRemoteActor
                            );
            //Use "typedActor" as a FooBar
        } catch (Exception e) {
            //dun care
        }
    }

    public interface HasName {
        String name();
    }

    static class Named implements HasName {
        private int id = new Random().nextInt(1024);

        @Override public String name() { return "name-" + id; }
    }


    @Test public void typedRouterPattern() {

            // prepare routees
            TypedActorExtension typed = TypedActor.get(system);

            HasName named1 = typed.typedActorOf(new TypedProps<Named>(Named.class));

                HasName named2 =
                   typed.typedActorOf(new TypedProps<Named>(Named.class));

            List<HasName> routees = new ArrayList<HasName>();
            routees.add(named1);
            routees.add(named2);

            List<String> routeePaths = new ArrayList<String>();
            routeePaths.add(typed.getActorRefFor(named1).path().toStringWithoutAddress());
            routeePaths.add(typed.getActorRefFor(named2).path().toStringWithoutAddress());

            // prepare untyped router
            ActorRef router = system.actorOf(new RoundRobinGroup(routeePaths).props(), "router");

            // prepare typed proxy, forwarding MethodCall messages to `router`
        HasName typedRouter = typed.typedActorOf(new TypedProps<Named>(Named.class), router);

            System.out.println("actor was: " + typedRouter.name()); // name-243
            System.out.println("actor was: " + typedRouter.name()); // name-614
            System.out.println("actor was: " + typedRouter.name()); // name-243
            System.out.println("actor was: " + typedRouter.name()); // name-614

            typed.poisonPill(named1);
            typed.poisonPill(named2);
            typed.poisonPill(typedRouter);
    }

    @Test
    public void childTest(){

        Squarer mySquarer = TypedActor.get(system).typedActorOf(
                new TypedProps<SquarerImpl>(Squarer.class,SquarerImpl.class));

        Squarer childSquarer = mySquarer.createChildSquarer();

        ActorRef actorRef = TypedActor.get(system).getActorRefFor(childSquarer);

        assertTrue(actorRef.isTerminated());

        assertTrue(childSquarer instanceof Squarer);

        assertTrue(TypedActor.get(system).isTypedActor(childSquarer));

    }
}