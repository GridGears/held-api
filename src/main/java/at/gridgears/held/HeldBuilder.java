package at.gridgears.held;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.net.URI;

public class HeldBuilder {

    private URI uri;
    private String language = "en";

    public HeldBuilder withURI(String uri) {
        this.uri = URI.create(uri);
        return this;
    }

    public HeldBuilder withLanguage(String language) {
        this.language = language;
        return this;
    }


    public Held build() {
        HeldClientConfig config = new HeldClientConfig(uri);
        ResponseParser responseParser = new ResponseParser(language);

        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();

        return new HeldClient(config, httpclient, responseParser);
    }
}
