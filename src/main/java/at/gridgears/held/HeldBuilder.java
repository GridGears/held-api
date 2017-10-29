package at.gridgears.held;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.net.URI;

public class HeldBuilder {

    private URI uri;

    public HeldBuilder withURI(String uri) {
        this.uri = URI.create(uri);
        return this;
    }


    public Held build() {
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();
        return new HeldClient(uri, httpclient, new ResponseParser());
    }
}
