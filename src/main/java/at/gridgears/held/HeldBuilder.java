package at.gridgears.held;

import java.net.URI;

public class HeldBuilder {

    private URI uri;

    public HeldBuilder withURI(URI uri) {


        return this;
    }


    public Held build() {
        return new HeldClient();
    }
}
