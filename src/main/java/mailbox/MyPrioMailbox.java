package mailbox;

import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.dispatch.Envelope;
import akka.dispatch.PriorityGenerator;
import akka.dispatch.UnboundedPriorityMailbox;
import com.typesafe.config.Config;

import java.util.Comparator;

public class MyPrioMailbox extends UnboundedPriorityMailbox {
    // needed for reflective instantiation
    public MyPrioMailbox(ActorSystem.Settings settings, Config config) {
        // Create a new PriorityGenerator, lower prio means more important
        super(new PriorityGenerator() {
            @Override
            public int gen(Object message) {
                if (message.equals("highpriority"))
                    return 0; // 'highpriority messages should be treated first if possible
                else if (message.equals("lowpriority"))
                    return 2; // 'lowpriority messages should be treated last if possible
                else if (message.equals(PoisonPill.getInstance()))
                    return 3; // PoisonPill when no other left
                else
                    return 1; // By default they go between high and low prio
            }
        });
    }

    @Override
    public Comparator<Envelope> cmp() {
        return new Comparator<Envelope>() {
            @Override
            public int compare(Envelope t1, Envelope t2) {
                  if(t1.message().equals("highpriority")) {
                      return 1;
                  };
                if(t2.message().equals("lowpriority")) {
                    return -1;
                }
                else return 0;
            }
        };
    }

    @Override
    public int initialCapacity() {
        return 10;
    }
}