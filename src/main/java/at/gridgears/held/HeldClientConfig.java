package at.gridgears.held;

import org.apache.commons.lang3.Validate;

import java.net.URI;

class HeldClientConfig {
    private final URI uri;

    HeldClientConfig(URI uri) {
        Validate.notNull(uri, "uri must not be null");
        this.uri = uri;
    }

    URI getUri() {
        return uri;
    }
}
