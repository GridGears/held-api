package at.gridgears.held.internal;

import at.gridgears.held.FindLocationRequest;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FindLocationRequestFactoryTest {

    private static final URI REQUEST_URI = URI.create("http://localhost/held");
    private static final List<Header> HEADERS = Arrays.asList(new BasicHeader("CustomHeader1", "CustomHeaderValue1"), new BasicHeader("CustomHeader2", "CustomHeaderValue2"));
    private static FindLocationRequestFactory requestFactory;

    @BeforeAll
    static void initFactory() {
        requestFactory = new FindLocationRequestFactory(HEADERS);
    }

    @ParameterizedTest
    @MethodSource("testParsingData")
    void httpContent(TestData testData) throws IOException {
        HttpPost request = requestFactory.createRequest(REQUEST_URI, testData.getRequest());

        String requestBody = EntityUtils.toString(request.getEntity());
        assertThat("request", requestBody, is(testData.getExpectedOutput()));
    }

    @Test
    void httpParameters() {
        HttpPost httpPost = requestFactory.createRequest(REQUEST_URI, new FindLocationRequest("identifier"));

        assertThat("Content-Type", httpPost.getFirstHeader("Content-Type").getValue(), is("application/held+xml;charset=utf-8"));
        assertThat("URI", httpPost.getURI(), is(REQUEST_URI));
        HEADERS.forEach(header -> assertThat(header.getName(), httpPost.getFirstHeader(header.getName()).getValue(), is(header.getValue())));
    }

    static Stream<TestData> testParsingData() {
        return Stream.of(
                new TestData(new FindLocationRequest("tel:123456789"),
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                "<locationRequest xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" xmlns:ns2=\"urn:ietf:params:xml:ns:geopriv:held:id\">\n" +
                                "   <ns2:device>\n" +
                                "        <ns2:uri>tel:123456789</ns2:uri>\n" +
                                "    </ns2:device>\n" +
                                "</locationRequest>"),
                new TestData(new FindLocationRequest("tel:123456789", Collections.singletonList(FindLocationRequest.LocationType.GEODETIC), true),
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                "<locationRequest xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" xmlns:ns2=\"urn:ietf:params:xml:ns:geopriv:held:id\">\n" +
                                "   <locationType exact=\"true\">\n" +
                                "       geodetic\n" +
                                "   </locationType>\n" +
                                "   <ns2:device>\n" +
                                "        <ns2:uri>tel:123456789</ns2:uri>\n" +
                                "    </ns2:device>\n" +
                                "</locationRequest>"),
                new TestData(new FindLocationRequest("tel:123456789", Arrays.asList(FindLocationRequest.LocationType.GEODETIC, FindLocationRequest.LocationType.CIVIC), false),
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                "<locationRequest xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" xmlns:ns2=\"urn:ietf:params:xml:ns:geopriv:held:id\">\n" +
                                "   <locationType exact=\"false\">\n" +
                                "       geodetic\n" +
                                "       civic\n" +
                                "   </locationType>\n" +
                                "   <ns2:device>\n" +
                                "        <ns2:uri>tel:123456789</ns2:uri>\n" +
                                "    </ns2:device>\n" +
                                "</locationRequest>"),
                new TestData(new FindLocationRequest("tel:123456789", Collections.emptyList(), false, FindLocationRequest.ResponseTime.createForDuration(Duration.ofSeconds(1L))),
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                "<locationRequest xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" xmlns:ns2=\"urn:ietf:params:xml:ns:geopriv:held:id\" responseTime=\"1000\">\n" +
                                "   <ns2:device>\n" +
                                "        <ns2:uri>tel:123456789</ns2:uri>\n" +
                                "    </ns2:device>\n" +
                                "</locationRequest>"),
                new TestData(new FindLocationRequest("tel:123456789", Collections.emptyList(), false, FindLocationRequest.ResponseTime.createForEmergencyRouting()),
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                "<locationRequest xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" xmlns:ns2=\"urn:ietf:params:xml:ns:geopriv:held:id\" responseTime=\"emergencyRouting\">\n" +
                                "   <ns2:device>\n" +
                                "        <ns2:uri>tel:123456789</ns2:uri>\n" +
                                "    </ns2:device>\n" +
                                "</locationRequest>"),
                new TestData(new FindLocationRequest("tel:123456789", Collections.emptyList(), false, FindLocationRequest.ResponseTime.createForEmergencyDispatch()),
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                "<locationRequest xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" xmlns:ns2=\"urn:ietf:params:xml:ns:geopriv:held:id\" responseTime=\"emergencyDispatch\">\n" +
                                "   <ns2:device>\n" +
                                "        <ns2:uri>tel:123456789</ns2:uri>\n" +
                                "    </ns2:device>\n" +
                                "</locationRequest>")
        );
    }

    private static class TestData {
        private final FindLocationRequest request;
        private final String expectedOutput;

        TestData(FindLocationRequest request, String expectedOutput) {
            this.request = request;
            this.expectedOutput = expectedOutput;
        }

        FindLocationRequest getRequest() {
            return request;
        }

        String getExpectedOutput() {
            return expectedOutput;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("request", request)
                    .append("expectedOutput", expectedOutput)
                    .toString();
        }
    }
}