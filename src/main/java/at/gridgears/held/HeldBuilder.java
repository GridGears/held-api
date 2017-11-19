package at.gridgears.held;

import at.gridgears.held.internal.HeldClient;
import at.gridgears.held.internal.HeldClientConfig;
import org.apache.commons.lang3.Validate;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

public class HeldBuilder {

    private URI uri;
    private String language = "en";
    private final List<Header> requestHeaders = new LinkedList<>();

    public HeldBuilder withURI(String uri) {
        Validate.notNull(uri, "uri must not be null");
        this.uri = URI.create(uri);
        return this;
    }

    public HeldBuilder withLanguage(String language) {
        Validate.notNull(language, "language must not be null");
        this.language = language;
        return this;
    }

    public HeldBuilder withHeader(String name, @Nullable String value) {
        Validate.notNull(name, "name must not be null");
        requestHeaders.add(new BasicHeader(name, value));
        return this;
    }


    public Held build() {
        HeldClientConfig config = new HeldClientConfig(uri, language, requestHeaders);
        return new HeldClient(config);
    }
}
