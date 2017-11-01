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
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class HeldClientTest {
    @Mock
    private CloseableHttpAsyncClient httpAsyncClient;

    @Mock
    private FindLocationCallback callBack;

    @Mock
    private ResponseParser responseParser;

    private URI uri = URI.create("http://gridgearstest/held");

    private LocationResult successLocationResult = LocationResult.createFoundResult("deviceIdentifier", Collections.singletonList(new Location(12.0, 13.0, 14.0, Instant.ofEpochSecond(12))));

    private HeldClient heldClient;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void initClient() {
        heldClient = new HeldClient(new HeldClientConfig(uri), httpAsyncClient, responseParser);
    }

    @Test
    void whenHttpClientIsNotRunningThenItIsStartedOnrequest() {
        when(httpAsyncClient.isRunning()).thenReturn(false);
        heldClient.findLocation("deviceIdentifier", callBack);

        verify(httpAsyncClient, times(1)).start();
    }

    @Test
    void whenHttpClientIsRunningThenItIsNotStartedOnrequest() {
        when(httpAsyncClient.isRunning()).thenReturn(true);
        heldClient.findLocation("deviceIdentifier", callBack);

        verify(httpAsyncClient, never()).start();
    }

    @Test
    void stopClosesHttpAsynClient() throws IOException {
        heldClient.stop();

        verify(httpAsyncClient).close();
    }

    @Test
    void correctGeodeticRequest() throws Exception {
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("http", 1, 1), HttpStatus.SC_OK, "SUCCESS"));
        response.setStatusCode(HttpStatus.SC_OK);
        response.setEntity(new StringEntity("location"));

        when(responseParser.parse("deviceIdentifier", "location")).thenReturn(successLocationResult);

        when(httpAsyncClient.execute(isA(HttpPost.class), isA(FutureCallback.class))).thenAnswer((Answer<Future<HttpResponse>>) invocationOnMock -> {
            ((FutureCallback) invocationOnMock.getArgument(1)).completed(response);
            return new CompletableFuture<>();
        });

        heldClient.findLocation("+43123456789", callBack);

        ArgumentCaptor<HttpPost> argumentCaptor = ArgumentCaptor.forClass(HttpPost.class);
        verify(httpAsyncClient).execute(argumentCaptor.capture(), isA(FutureCallback.class));

        HttpPost httpPost = argumentCaptor.getValue();
        assertThat("Content-Type", httpPost.getFirstHeader("Content-Type").getValue(), is("application/held+xml;charset=utf-8"));

        String request = EntityUtils.toString(httpPost.getEntity());
        EntityUtils.consume(httpPost.getEntity());

        assertThat("request", request, is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<locationRequest xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" xmlns:ns2=\"urn:ietf:params:xml:ns:geopriv:held:id\">\n   <locationType exact=\"true\">geodetic</locationType>   <ns2:device>\n        <ns2:uri>+43123456789</ns2:uri>\n    </ns2:device>\n</locationRequest>"));
    }

    @Test
    void correctSuccessLocationResult() throws Exception {
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("http", 1, 1), HttpStatus.SC_OK, "SUCCESS"));
        response.setStatusCode(HttpStatus.SC_OK);
        response.setEntity(new StringEntity("location"));

        when(responseParser.parse("deviceIdentifier", "location")).thenReturn(successLocationResult);

        when(httpAsyncClient.execute(isA(HttpPost.class), isA(FutureCallback.class))).thenAnswer((Answer<Future<HttpResponse>>) invocationOnMock -> {
            ((FutureCallback) invocationOnMock.getArgument(1)).completed(response);
            return new CompletableFuture<>();
        });

        heldClient.findLocation("deviceIdentifier", callBack);

        verify(callBack).success(successLocationResult);
    }
}