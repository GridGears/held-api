package at.gridgears.held;


import org.apache.commons.lang3.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;

public class HeldClient implements Held {
    private static final Logger LOG = LogManager.getLogger();

    private final URI uri;
    private final CloseableHttpAsyncClient httpclient;
    private final ResponseParser responseParser;

    public HeldClient(URI uri, CloseableHttpAsyncClient httpclient, ResponseParser responseParser) {
        Validate.notNull(uri, "Uri must not  be null");
        Validate.notNull(httpclient, "httpclient must not  be null");
        Validate.notNull(responseParser, "responseParser must not  be null");

        this.uri = uri;
        this.httpclient = httpclient;
        this.responseParser = responseParser;
    }

    @Override
    public void findLocation(String identifier, LocationRequestCallback callback) {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader(new BasicHeader("Content-Type", "held+xml;charset=utf-8"));

        httpclient.execute(httpPost, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse response) {
                LocationResult locationResult;
                try {
                    String heldResponse = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                    LOG.debug("Received response for {}: {}", identifier, heldResponse);
                    locationResult = responseParser.parse(heldResponse);
                    callback.success(locationResult);
                } catch (ResponseParsingException e) {
                    LOG.warn("Could not parse response content", e);
                    callback.failed(e);
                } catch (IOException e) {
                    LOG.warn("Could not extract response content", e);
                    callback.failed(e);
                }
            }

            @Override
            public void failed(Exception e) {
                LOG.warn("Error during HELD request", e);
                callback.failed(e);
            }

            @Override
            public void cancelled() {
                callback.failed(new RequestAbortedException("Request cancelled"));
            }
        });
    }
}
