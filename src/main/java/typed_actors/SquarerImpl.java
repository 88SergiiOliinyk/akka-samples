package typed_actors;

import akka.dispatch.Futures;
import akka.japi.Option;
import scala.concurrent.Future;

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
        }