package event_bus;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.japi.LookupEventBus;import java.lang.Object;import java.lang.Override;import java.lang.String;

/**
 * Java example showing the usage of a Akka {@link akka.event.japi.LookupEventBus}.
 * The case below shows publishing events to two different "channels". A channel is just
 * a string value carried along with the Event.
 */
public class AkkaEventBusExample {
    public class Event {
        private String channel;

        public Event(final String channel) {
            this.channel = channel;
        }

        public String getChannel() {
            return channel;
        }
    }

    public class SomethingHappenedEvent extends Event {
        public SomethingHappenedEvent(String channel) {
            super(channel);
        }
    }

    public class SomethingElseHappenedEvent extends Event {
        public SomethingElseHappenedEvent(String channel) {
            super(channel);
        }
    }

    /**
     * Way to send events to multiple subscribers. Based on Akka {@link akka.event.japi.LookupEventBus}.
     */
    public class EventBus extends LookupEventBus {
        /**
         * Initial size of the index data structure used internally
         * (i.e. the expected number of different classifiers)
         */
        @Override
        public int mapSize() {
            return 5;
        }

        /**
         * Used to define a partial ordering of subscribers. The ordering is based on Event.channel
         */
        @Override
        public int compareSubscribers(Object subscriberA, Object subscriberB) {
            return ((Event)subscriberA).getChannel().compareTo(((Event)subscriberB).getChannel());
        }

        /**
         * Extract the classification data from the event.
         * @param event {@link Event} to classify
         * @return Channel string from the {@link Event}
         */
        @Override
        public Object classify(Object event) {
            return ((Event)event).getChannel();
        }

        /**
         * Publish an {@link Event}
         * @param event {@link Event} to publish
         * @param subscriber {@link akka.actor.ActorRef} that is subscribed to the {@link Event}
         */
        @Override
        public void publish(Object event, Object subscriber) {
            ((ActorRef) subscriber).tell(event, ActorRef.noSender());
        }
    }

    public void createBusAndPublishEvents() {
        // Create the EventBus and the ActorSystem instance.
        final EventBus eventBus = new EventBus();
        final ActorSystem actorSystem = ActorSystem.create("Events");

        // Create two different actor instances. The instances will be subscribed to
        // different channels
        final ActorRef actor = actorSystem.actorOf(Props.create(FirstActor.class));
        final ActorRef actor2 = actorSystem.actorOf(Props.create(FirstActor.class));

        // Subscribe the two actors to the two different channels
        String CHANNEL1 = "channel1";
        String CHANNEL2 = "channel2";
        eventBus.subscribe(actor, CHANNEL1);
        eventBus.subscribe(actor2, CHANNEL2);

        // Publish a couple of events to the two channels.
        // Publish to CHANNEL1
        eventBus.publish(new SomethingHappenedEvent(CHANNEL1));

        // Publish to CHANNEL2
        eventBus.publish(new SomethingElseHappenedEvent(CHANNEL2));

        actorSystem.shutdown();
    }



    /**
     * This is the Actor implementation. i.e. The object that is being subscribed to listen for events.
     * aka: Observer.
     */


    public static void main(String[] args) {
        new AkkaEventBusExample().createBusAndPublishEvents();
    }
}