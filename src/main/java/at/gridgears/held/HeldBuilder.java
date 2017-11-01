package at.gridgears.held;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.net.URI;

public class HeldBuilder {

    private URI uri;
    private String language = "en";
    private Authorization authorization = new NoAuthorization();

    public HeldBuilder withURI(String uri) {
        this.uri = URI.create(uri);
        return this;
    }

    public HeldBuilder withLanguage(String language) {
        this.language = language;
        return this;
    }

    public HeldBuilder withBasicAuthentication(String token) {
        this.authorization = new BasicHeaderAuthorization(token);
        return this;
    }


    public Held build() {
        ResponseParser responseParser = new ResponseParser(language);

        LocationRequestFactory locationRequestFactory = new LocationRequestFactory(authorization);

        HeldClientConfig config = new HeldClientConfig(uri);

        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();

        return new HeldClient(config, httpclient, responseParser, locationRequestFactory);
    }
}
