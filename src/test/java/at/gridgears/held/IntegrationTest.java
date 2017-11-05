package at.gridgears.held;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD.TooManyStaticImports")
class IntegrationTest {
    private static final Logger LOG = LogManager.getLogger();
    private static final Duration TIMEOUT = Duration.ofSeconds(5L);
    private static final Header AUTHENTICATION_HEADER = new BasicHeader("authentication", "token");
    private static LocalTestServer server;
    private static Held held;
    private static String path = "/heldtest/";

    @BeforeAll
    static void setupTestServer() throws Exception {
        server = new LocalTestServer(null, null);
        server.start();

        String serverUrl = "http://" + server.getServiceHostName() + ":" + server.getServicePort();
        LOG.info("LocalTestServer available at {}", serverUrl);
    }

    @BeforeAll
    static void setupHeld() {
        String uri = "http://127.0.0.1:" + server.getServicePort() + path;
        held = new HeldBuilder().withURI(uri).withHeader(AUTHENTICATION_HEADER.getName(), AUTHENTICATION_HEADER.getValue()).build();
    }

    @AfterAll
    static void stopTestServer() throws Exception {
        server.stop();
    }

    @Test
    @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
    void successfulRequest() throws InterruptedException {
        assertTimeoutPreemptively(TIMEOUT, () -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            prepareResponse(getSuccessLocationResponse());

            held.findLocation("identifier", new FindLocationCallback() {
                @Override
                public void success(LocationResult locationResult) {
                    assertThat("status", locationResult.getStatus().getStatusCode(), is(LocationResult.StatusCode.LOCATION_FOUND));
                    List<Location> locations = locationResult.getLocations();
                    assertThat("identifier", locationResult.getIdentifier(), is("identifier"));
                    assertThat("result size", locations, hasSize(1));
                    countDownLatch.countDown();
                }

                @Override
                public void failed(Exception e) {
                    LOG.error("Error occurred", e);
                    fail("Exception occurred");
                }
            });

            countDownLatch.await();
        });
    }


    @Test
    @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
    void recoverableErrorResponse() throws InterruptedException {
        assertTimeoutPreemptively(TIMEOUT, () -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            prepareResponse(getNotFoundLocationResponse());

            held.findLocation("identifier", new FindLocationCallback() {
                @Override
                public void success(LocationResult locationResult) {
                    assertThat("status", locationResult.getStatus().getStatusCode(), is(LocationResult.StatusCode.LOCATION_UNKNOWN));
                    assertThat("result size", locationResult.getLocations(), empty());
                    countDownLatch.countDown();
                }

                @Override
                public void failed(Exception e) {
                    LOG.error("Error occurred", e);
                    fail("Exception occurred");
                }
            });

            countDownLatch.await();
        });
    }

    @Test
    @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
    void unRecoverableErrorResponse() throws InterruptedException {
        assertTimeoutPreemptively(TIMEOUT, () -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            prepareResponse(getXmlErrorErrorResponse());

            held.findLocation("identifier", new FindLocationCallback() {
                @Override
                public void success(LocationResult locationResult) {
                    fail("Expected an exception");
                }

                @Override
                public void failed(Exception e) {
                    assertThat("exception message", e.getMessage(), is("xmlError: Invalid XML"));
                    countDownLatch.countDown();
                }
            });

            countDownLatch.await();
        });
    }

    @Test
    @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
    void httpErrorStatus() throws InterruptedException {
        assertTimeoutPreemptively(TIMEOUT, () -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            server.register(path, (httpRequest, httpResponse, httpContext) -> {
                httpResponse.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            });

            held.findLocation("identifier", new FindLocationCallback() {
                @Override
                public void success(LocationResult locationResult) {
                    fail("Expected an exception");
                }

                @Override
                public void failed(Exception e) {
                    assertThat("exception message", e.getMessage(), is("HTTP error: 400: Bad Request"));
                    countDownLatch.countDown();
                }
            });

            countDownLatch.await();
        });
    }

    private void prepareResponse(String responseContent) {
        server.register(path, (httpRequest, httpResponse, httpContext) -> {
            int statusCode = verifyRequest(httpRequest);
            httpResponse.setStatusCode(statusCode);

            if (statusCode == HttpStatus.SC_OK) {
                ContentType contentType = ContentType.create("application/held+xml", new BasicNameValuePair("charset", "utf-8"));
                httpResponse.setEntity(new StringEntity(responseContent, contentType));
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
                "           <message xml:lang=\"en\">Unable to determine location\n" +
                "           </message>\n" +
                "         </error>";
    }

    private String getXmlErrorErrorResponse() {
        return "<?xml version=\"1.0\"?>\n" +
                "         <error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\"\n" +
                "            code=\"xmlError\">\n" +
                "           <message xml:lang=\"en\">Invalid XML\n" +
                "           </message>\n" +
                "         </error>";
    }


}
