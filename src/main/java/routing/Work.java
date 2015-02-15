package routing;

import java.io.Serializable;

/**
 * Created by Sergioli on 2/8/15.
 */
public final class Work implements Serializable{

    public static final Long serialVersionUID = 1L;

    public final String payload;

    public Work(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Work{" +
                "payload='" + payload + '\'' +
                '}';
    }
}
