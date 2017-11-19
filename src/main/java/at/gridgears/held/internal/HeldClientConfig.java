package at.gridgears.held.internal;

import org.apache.commons.lang3.Validate;
import org.apache.http.Header;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeldClientConfig {
    private final URI uri;
    private final String language;
    private final List<Header> requestHeaders;

    public HeldClientConfig(URI uri, String language, List<Header> requestHeaders) {
        Validate.notNull(uri, "uri must not be null");
        Validate.notNull(language, "language must not be null");
        Validate.noNullElements(requestHeaders, "requestHeaders must not be null or contain null elements");
        this.language = language;
        this.requestHeaders = Collections.unmodifiableList(new ArrayList<>(requestHeaders));

        this.uri = uri;
    }

    URI getUri() {
        return uri;
    }

    String getLanguage() {
        return language;
    }

    List<Header> getRequestHeaders() {
        return requestHeaders;
    }
}
