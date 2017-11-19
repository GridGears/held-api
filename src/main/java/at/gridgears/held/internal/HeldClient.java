package at.gridgears.held.internal;


import at.gridgears.held.FindLocationCallback;
import at.gridgears.held.FindLocationRequest;
import at.gridgears.held.FindLocationResult;
import at.gridgears.held.Held;
import at.gridgears.held.internal.parser.ResponseParser;
import at.gridgears.held.internal.parser.ResponseParsingException;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;

public class HeldClient implements Held {
    private static final Logger LOG = LogManager.getLogger();

    private final FindLocationRequestFactory findLocationRequestFactory;
    private final URI uri;
    private final CloseableHttpAsyncClient httpclient;
    private final ResponseParser responseParser;

    public HeldClient(HeldClientConfig config) {
        this(config, HttpAsyncClients.createDefault(), new ResponseParser(config.getLanguage()), new FindLocationRequestFactory(config.getRequestHeaders()));
    }

    HeldClient(HeldClientConfig config, CloseableHttpAsyncClient httpclient, ResponseParser responseParser, FindLocationRequestFactory findLocationRequestFactory) {
        Validate.notNull(config, "config must not be null");
        Validate.notNull(httpclient, "httpclient must not  be null");
        Validate.notNull(responseParser, "responseParser must not  be null");
        Validate.notNull(findLocationRequestFactory, "findLocationRequestFactory must not  be null");

        this.uri = config.getUri();
        this.httpclient = httpclient;
        this.responseParser = responseParser;
        this.findLocationRequestFactory = findLocationRequestFactory;
    }

    @Override
    public void findLocation(FindLocationRequest request, FindLocationCallback callback) {
        Validate.notNull(request, "request must not be null");
        Validate.notNull(callback, "callback must not be null");
        HttpPost httpPost = findLocationRequestFactory.createRequest(uri, request);

        startHttpClientIfNecessary();
        httpclient.execute(httpPost, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse response) {
                FindLocationResult findLocationResult;
                try {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        String heldResponse = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
                        LOG.debug("Received response for deviceIdentifier '{}': {}", request.getIdentifier(), heldResponse);
                        findLocationResult = responseParser.parse(heldResponse);
                        callback.completed(request, findLocationResult);
                    } else {
                        callback.failed(request, new HeldException("HTTP error", statusCode + ": " + response.getStatusLine().getReasonPhrase()));
                    }
                } catch (ResponseParsingException e) {
                    LOG.warn("Could not parse response content", e);
                    callback.failed(request, e);
                } catch (IOException e) {
                    LOG.warn("Could not extract response content", e);
                    callback.failed(request, e);
                }
            }

            @Override
            public void failed(Exception e) {
                LOG.warn("Error during HELD request", e);
                callback.failed(request, e);
            }

            @Override
            public void cancelled() {
                callback.failed(request, new RequestAbortedException("Request cancelled"));
            }
        });
    }

    private void startHttpClientIfNecessary() {
        if (!httpclient.isRunning()) {
            httpclient.start();
        }
    }

    public void stop() {
        try {
            httpclient.close();
        } catch (IOException e) {
            LOG.error("Exception while closing HttpClient", e);
        }
    }
}
