package at.gridgears.held;


import java.net.URI;
import java.util.function.Consumer;

public class HeldClient implements Held {
    private final URI uri;

    public HeldClient(URI uri) {
        this.uri = uri;
    }


    @Override
    public void findLocation(String identifier, Consumer<LocationResult> callback) {
        System.out.println(uri);
    }
}
