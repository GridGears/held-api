package at.gridgears.held;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalTestServer;
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
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.fail;

class IntegrationTest {
    private static final Logger LOG = LogManager.getLogger();
    private static final Duration TIMEOUT = Duration.ofSeconds(5L);
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
        held = new HeldBuilder().withURI(uri).build();
    }

    @AfterAll
    static void stopTestServer() throws Exception {
        server.stop();
    }

    @Test
    @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
    void successHeldRequest() throws InterruptedException {
        assertTimeout(TIMEOUT, () -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            server.register(path, (httpRequest, httpResponse, httpContext) -> {
                httpResponse.setStatusCode(HttpStatus.SC_OK);
                ContentType contentType = ContentType.create("application/held+xml", new BasicNameValuePair("charset", "utf-8"));
                httpResponse.setEntity(new StringEntity(getSuccessLocationResponse(), contentType));
            });

            held.findLocation("identifier", new FindLocationCallback() {
                @Override
                public void success(LocationResult locationResult) {
                    List<Location> locations = locationResult.getLocations();
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
}
