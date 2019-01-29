package at.gridgears.integrationtest;

import at.gridgears.held.FindLocationCallback;
import at.gridgears.held.FindLocationError;
import at.gridgears.held.FindLocationRequest;
import at.gridgears.held.FindLocationResult;
import at.gridgears.held.Held;
import at.gridgears.held.HeldBuilder;
import at.gridgears.held.Location;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalServerTestBase;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.JUnit4TestShouldUseAfterAnnotation"})
class HeldIntegrationTest extends LocalServerTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(HeldIntegrationTest.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(5L);
    private static final Header AUTHENTICATION_HEADER = new BasicHeader("authentication", "token");
    private Held held;
    private static final String PATH = "/heldtest/";
    private final AtomicReference<String> responseContent = new AtomicReference<>();
    private final AtomicInteger responseStatusCode = new AtomicInteger(-1);

    @BeforeEach
    void setupTestServer() throws Exception {
        setUp();
        registerHandler();
        HttpHost server = start();

        String serverUrl = "http://" + server.getHostName() + ":" + server.getPort();
        LOG.info("LocalTestServer available at {}", serverUrl);

        String uri = "http://" + server.getHostName() + ':' + server.getPort() + PATH;
        held = new HeldBuilder().withURI(uri).withHeader(AUTHENTICATION_HEADER.getName(), AUTHENTICATION_HEADER.getValue()).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (this.httpclient != null) {
            this.httpclient.close();
        }

        if (this.server != null) {
            this.server.shutdown(2L, TimeUnit.SECONDS);
        }
    }

    @Test
    @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
    void successfulRequest() {
        assertTimeoutPreemptively(TIMEOUT, () -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            prepareResponse(getSuccessLocationResponse());

            FindLocationRequest request = new FindLocationRequest("identifier");

            held.findLocation(request, new FindLocationCallback() {
                @Override
                public void completed(FindLocationRequest request, FindLocationResult findLocationResult) {
                    assertThat("status", findLocationResult.getStatus(), is(FindLocationResult.Status.FOUND));
                    assertThat("error", findLocationResult.getError().isPresent(), is(false));
                    assertThat("no error", findLocationResult.getError().isPresent(), is(false));
                    List<Location> locations = findLocationResult.getLocations();
                    assertThat("identifier", request.getIdentifier(), is("identifier"));
                    assertThat("result size", locations, hasSize(1));
                    assertThat("references", findLocationResult.getLocationReferences().iterator().next().getUri(), is(URI.create("https://ls.example.com:9768/357yc6s64ceyoiuy5ax3o")));
                    countDownLatch.countDown();
                }

                @Override
                public void failed(FindLocationRequest request, Exception e) {
                    LOG.error("Error occurred", e);
                    fail("Exception occurred");
                }
            });

            countDownLatch.await();
        });
    }


    @Test
    @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
    void notFoundResponse() {
        assertTimeoutPreemptively(TIMEOUT, () -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            prepareResponse(getNotFoundLocationResponse());

            FindLocationRequest request = new FindLocationRequest("identifier");
            held.findLocation(request, new FindLocationCallback() {
                @Override
                public void completed(FindLocationRequest request, FindLocationResult findLocationResult) {
                    assertThat("status", findLocationResult.getStatus(), is(FindLocationResult.Status.NOT_FOUND));
                    assertThat("error", findLocationResult.getError().get(), is(new FindLocationError("locationUnknown", "error message")));
                    assertThat("no location results", findLocationResult.getLocations(), empty());
                    countDownLatch.countDown();
                }

                @Override
                public void failed(FindLocationRequest request, Exception e) {
                    LOG.error("Error occurred", e);
                    fail("Exception occurred");
                }
            });

            countDownLatch.await();
        });
    }

    @Test
    @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
    void errorResponse() {
        assertTimeoutPreemptively(TIMEOUT, () -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            prepareResponse(getUnsupportedMessageResponse());

            FindLocationRequest request = new FindLocationRequest("identifier");
            held.findLocation(request, new FindLocationCallback() {
                @Override
                public void completed(FindLocationRequest request, FindLocationResult findLocationResult) {
                    assertThat("status", findLocationResult.getStatus(), is(FindLocationResult.Status.ERROR));
                    assertThat("error", findLocationResult.getError().get(), is(new FindLocationError("unsupportedMessage", "error message")));
                    assertThat("no location results", findLocationResult.getLocations(), empty());
                    countDownLatch.countDown();
                }

                @Override
                public void failed(FindLocationRequest request, Exception e) {
                    LOG.error("Error occurred", e);
                    fail("Exception occurred");
                }
            });

            countDownLatch.await();
        });
    }

    @Test
    @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
    void httpErrorStatus() {
        assertTimeoutPreemptively(TIMEOUT, () -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            prepareResponse("", HttpStatus.SC_BAD_REQUEST);

            FindLocationRequest request = new FindLocationRequest("identifier");
            held.findLocation(request, new FindLocationCallback() {
                @Override
                public void completed(FindLocationRequest request, FindLocationResult findLocationResult) {
                    fail("Expected an exception");
                }

                @Override
                public void failed(FindLocationRequest request, Exception e) {
                    assertThat("identifier", request.getIdentifier(), is("identifier"));
                    assertThat("exception message", e.getMessage(), is("HTTP error: 400: Bad Request"));
                    countDownLatch.countDown();
                }
            });

            countDownLatch.await();
        });
    }

    private void prepareResponse(String responseContent) {
        this.responseContent.set(responseContent);
    }

    private void prepareResponse(String responseContent, int statusCode) {
        prepareResponse(responseContent);
        this.responseStatusCode.set(statusCode);
    }

    private void registerHandler() {
        serverBootstrap.registerHandler(PATH, (httpRequest, httpResponse, httpContext) -> {
            int statusCode = verifyRequest(httpRequest);
            httpResponse.setStatusCode(statusCode);

            if (statusCode == HttpStatus.SC_OK) {
                ContentType contentType = ContentType.create("application/held+xml", new BasicNameValuePair("charset", "utf-8"));
                httpResponse.setEntity(new StringEntity(this.responseContent.get(), contentType));
            }

            if (responseStatusCode.get() != -1) {
                httpResponse.setStatusCode(responseStatusCode.get());
            }
        });
    }

    private int verifyRequest(HttpRequest httpRequest) {
        int result;
        Header authorizationHeader = httpRequest.getFirstHeader(AUTHENTICATION_HEADER.getName());
        if (authorizationHeader == null || !authorizationHeader.getValue().equals(AUTHENTICATION_HEADER.getValue())) {
            result = HttpStatus.SC_UNAUTHORIZED;
        } else {
            result = HttpStatus.SC_OK;
        }
        return result;
    }

    private String getSuccessLocationResponse() {
        return "<?xml version=\"1.0\"?>\n" +
                "    <locationResponse xmlns=\"urn:ietf:params:xml:ns:geopriv:held\">\n" +
                "<locationUriSet expires=\"2006-01-01T13:00:00.0Z\">\n" +
                "       <locationURI>https://ls.example.com:9768/357yc6s64ceyoiuy5ax3o\n" +
                "       </locationURI>\n" +
                "     </locationUriSet>" +
                "     <presence xmlns=\"urn:ietf:params:xml:ns:pidf\"\n" +
                "      entity=\"pres:3650n87934c@ls.example.com\">\n" +
                "      <tuple id=\"b650sf789nd\">\n" +
                "       <status>\n" +
                "        <geopriv xmlns=\"urn:ietf:params:xml:ns:pidf:geopriv10\">\n" +
                "         <location-info>\n" +
                "          <Point xmlns=\"http://www.opengis.net/gml\"\n" +
                "           srsName=\"urn:ogc:def:crs:EPSG::4326\">\n" +
                "           <pos>-34.407 150.88001</pos>\n" +
                "          </Point>\n" +
                "         </location-info>\n" +
                "         <usage-rules\n" +
                "          xmlns:gbp=\"urn:ietf:params:xml:ns:pidf:geopriv10:basicPolicy\">\n" +
                "          <gbp:retention-expiry>2006-01-11T03:42:28+00:00\n" +
                "          </gbp:retention-expiry>\n" +
                "         </usage-rules>\n" +
                "         <method>Wiremap</method>\n" +
                "        </geopriv>\n" +
                "       </status>\n" +
                "       <timestamp>2006-01-10T03:42:28+00:00</timestamp>\n" +
                "      </tuple>\n" +
                "     </presence>\n" +
                "    </locationResponse>";
    }

    private String getNotFoundLocationResponse() {
        return "<?xml version=\"1.0\"?>\n" +
                "         <error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\"\n" +
                "            code=\"locationUnknown\">\n" +
                "           <message xml:lang=\"en\">error message\n" +
                "           </message>\n" +
                "         </error>";
    }

    public String getUnsupportedMessageResponse() {
        return "<?xml version=\"1.0\"?>\n" +
                "         <error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\"\n" +
                "            code=\"unsupportedMessage\">\n" +
                "           <message xml:lang=\"en\">error message\n" +
                "           </message>\n" +
                "         </error>";
    }
}
