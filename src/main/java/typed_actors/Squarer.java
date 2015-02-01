package typed_actors;

import akka.japi.Option;
import scala.concurrent.Future;

/**
 * Created by Sergioli on 1/31/15.
 */
public interface Squarer {
    void squareDontCare(int i); //fire-forget

    Future<Integer> square(int i); //non-blocking send-request-reply

    Option<Integer> squareNowPlease(int i);//blocking send-request-reply

    int squareNow(int i); //blocking send-request-reply
}