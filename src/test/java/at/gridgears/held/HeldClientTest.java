package at.gridgears.held;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HeldClientTest {
    @Mock
    private CloseableHttpAsyncClient httpAsyncClient;

    @Mock
    private FindLocationCallback callBack;

    @Mock
    private ResponseParser responseParser;

    private URI uri = URI.create("http://gridgearstest/held");

    private LocationResult successLocationResult = new LocationResult(Collections.singletonList(new Location(12.0, 13.0, 14.0, Instant.ofEpochSecond(12))));

    private HeldClient heldClient;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void initClient() {
        heldClient = new HeldClient(uri, httpAsyncClient, responseParser);
    }

    @Test
    void correctSuccessLocationResult() throws Exception {
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("http", 1, 1), HttpStatus.SC_OK, "SUCCESS"));
        response.setStatusCode(HttpStatus.SC_OK);
        response.setEntity(new StringEntity("location"));

        when(responseParser.parse("location")).thenReturn(successLocationResult);

        when(httpAsyncClient.execute(isA(HttpPost.class), isA(FutureCallback.class))).thenAnswer((Answer<Future<HttpResponse>>) invocationOnMock -> {
            ((FutureCallback) invocationOnMock.getArgument(1)).completed(response);
            return new CompletableFuture<>();
        });

        heldClient.findLocation("locationIdentifier", callBack);

        verify(callBack).success(successLocationResult);
    }
}