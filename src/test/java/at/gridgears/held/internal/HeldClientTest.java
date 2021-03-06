package at.gridgears.held.internal;

import at.gridgears.held.FindLocationCallback;
import at.gridgears.held.FindLocationRequest;
import at.gridgears.held.FindLocationResult;
import at.gridgears.held.Location;
import at.gridgears.held.internal.parser.ResponseParser;
import org.apache.http.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class HeldClientTest {
    private static final String DEVICE_IDENTIFIER = "+43123456789";
    private static final String RESPONSE_CONTENT = "responseContent";

    @Mock
    private CloseableHttpAsyncClient httpAsyncClient;

    @Mock
    private FindLocationCallback callBack;

    @Mock
    private ResponseParser responseParser;

    @Mock
    private FindLocationRequestFactory findLocationRequestFactory;

    @Mock
    private HttpPost httpPost;

    private final URI uri = URI.create("http://gridgearstest/held");

    private final FindLocationResult successFindLocationResult = FindLocationResult.createFoundResult(Collections.singletonList(new Location(12.0, 13.0, 14.0, Instant.ofEpochSecond(12))), Collections.emptyList(), Collections.emptyList(), RESPONSE_CONTENT);

    private final FindLocationRequest findLocationRequest = new FindLocationRequest(DEVICE_IDENTIFIER);

    private final List<Header> expectedHeaders = new ArrayList<>();

    private HeldClient heldClient;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void initClient() {
        expectedHeaders.add(new BasicHeader("CustomHeader", "CustomHeaderValue"));

        when(findLocationRequestFactory.createRequest(uri, findLocationRequest)).thenReturn(httpPost);

        heldClient = new HeldClient(new HeldClientConfig(uri, "en", expectedHeaders), httpAsyncClient, responseParser, findLocationRequestFactory);
    }

    @Test
    void whenHttpClientIsNotRunningThenItIsStartedOnRequest() {
        when(httpAsyncClient.isRunning()).thenReturn(false);
        heldClient.findLocation(findLocationRequest, callBack);

        verify(httpAsyncClient, times(1)).start();
    }

    @Test
    void whenHttpClientIsRunningThenItIsNotStartedOnrequest() {
        when(httpAsyncClient.isRunning()).thenReturn(true);
        heldClient.findLocation(findLocationRequest, callBack);

        verify(httpAsyncClient, never()).start();
    }

    @Test
    void stopClosesHttpAsynClient() throws IOException {
        heldClient.stop();

        verify(httpAsyncClient).close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void correctSuccessLocationResult() throws Exception {
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("http", 1, 1), HttpStatus.SC_OK, "SUCCESS"));
        response.setStatusCode(HttpStatus.SC_OK);
        response.setEntity(new StringEntity("location"));

        when(responseParser.parse("location")).thenReturn(successFindLocationResult);

        HttpEntity entity = new StringEntity("requestString");
        when(httpPost.getEntity()).thenReturn(entity);
        when(httpAsyncClient.execute(eq(httpPost), isA(FutureCallback.class))).thenAnswer((Answer<Future<HttpResponse>>) invocationOnMock -> {
            ((FutureCallback) invocationOnMock.getArgument(1)).completed(response);
            return new CompletableFuture<>();
        });

        heldClient.findLocation(findLocationRequest, callBack);

        verify(callBack).completed(findLocationRequest, successFindLocationResult);

        assertThat("request set on response object", successFindLocationResult.getRawRequest(), is("requestString"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void correctSuccessLocationResultWithOnlyReference() throws Exception {
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("http", 1, 1), HttpStatus.SC_OK, "SUCCESS"));
        response.setStatusCode(HttpStatus.SC_OK);
        response.setEntity(new StringEntity("location"));

        when(responseParser.parse("location")).thenReturn(successFindLocationResult);

        HttpEntity entity = new StringEntity("requestString");
        when(httpPost.getEntity()).thenReturn(entity);
        when(httpAsyncClient.execute(eq(httpPost), isA(FutureCallback.class))).thenAnswer((Answer<Future<HttpResponse>>) invocationOnMock -> {
            ((FutureCallback) invocationOnMock.getArgument(1)).completed(response);
            return new CompletableFuture<>();
        });

        heldClient.findLocation(findLocationRequest, callBack);

        verify(callBack).completed(findLocationRequest, successFindLocationResult);

        assertThat("request set on response object", successFindLocationResult.getRawRequest(), is("requestString"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void correctHttpErrorStatusCodeResult() throws Exception {
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("http", 1, 1), HttpStatus.SC_BAD_REQUEST, "Bad Request"));

        when(responseParser.parse("location")).thenReturn(successFindLocationResult);

        when(httpAsyncClient.execute(eq(httpPost), isA(FutureCallback.class))).thenAnswer((Answer<Future<HttpResponse>>) invocationOnMock -> {
            ((FutureCallback) invocationOnMock.getArgument(1)).completed(response);
            return new CompletableFuture<>();
        });

        heldClient.findLocation(findLocationRequest, callBack);

        ArgumentCaptor<HeldException> argumentCaptor = ArgumentCaptor.forClass(HeldException.class);
        verify(callBack).failed(eq(findLocationRequest), argumentCaptor.capture());
        assertThat("correct exception", argumentCaptor.getValue().getMessage(), is("HTTP error: 400: Bad Request"));
    }
}