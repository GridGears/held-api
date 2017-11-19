package at.gridgears.held.internal;

import at.gridgears.held.Location;
import at.gridgears.held.FindLocationResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class ResponseParserTest {
    private static ResponseParser parser;

    @BeforeAll
    static void initParser() {
        parser = new ResponseParser("en");
    }

    @ParameterizedTest
    @MethodSource("testParsingData")
    void testParsing(TestParsingData testData) throws Exception {
        FindLocationResult result = parser.parse(testData.getInput());
        assertThat("output", result, is(testData.getExpectedOutput()));
    }

    @ParameterizedTest
    @MethodSource("testParsingWithExceptionData")
    void testParsingWithException(TestParsingWithExceptionData testData) throws ResponseParsingException {
        try {
            parser.parse(testData.getInput());
            fail("Expected exception: " + testData.getExpectedException());
        } catch (HeldException e) {
            assertThat("exception", e.getMessage(), is(testData.getExpectedException().getMessage()));
        }

    }

    @Test()
    void parsingInvalidResponseThrowsException() {
        assertThrows(ResponseParsingException.class,
                () -> parser.parse("invalid input"));
    }

    static Stream<TestParsingData> testParsingData() {
        return Stream.of(
                new TestParsingData("Point",
                        "<?xml version=\"1.0\"?>\n" +
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
                                "       <timestamp>1970-01-01T00:00:10+00:00</timestamp>\n" +
                                "      </tuple>\n" +
                                "     </presence>\n" +
                                "    </locationResponse>",
                        FindLocationResult.createFoundResult(Collections.singletonList(new Location(-34.407, 150.88001, 0.0, Instant.ofEpochSecond(10))))),
                new TestParsingData("Circle",
                        "<?xml version=\"1.0\"?>\n" +
                                "    <locationResponse xmlns=\"urn:ietf:params:xml:ns:geopriv:held\">\n" +
                                "     <presence xmlns=\"urn:ietf:params:xml:ns:pidf\"\n" +
                                "      entity=\"pres:3650n87934c@ls.example.com\">\n" +
                                "      <tuple id=\"b650sf789nd\">\n" +
                                "       <status>\n" +
                                "        <geopriv xmlns=\"urn:ietf:params:xml:ns:pidf:geopriv10\">\n" +
                                "         <location-info>\n" +
                                "           <gs:Circle xmlns:gs=\"http://www.opengis.net/pidflo/1.0\"\n" +
                                "               xmlns:gml=\"http://www.opengis.net/gml\"\n" +
                                "               srsName=\"urn:ogc:def:crs:EPSG::4326\">\n" +
                                "               <gml:pos>-34.407 150.88001</gml:pos>\n" +
                                "               <gs:radius uom=\"urn:ogc:def:uom:EPSG::9001\">30\n" +
                                "               </gs:radius>\n" +
                                "           </gs:Circle>" +
                                "         </location-info>\n" +
                                "         <usage-rules\n" +
                                "          xmlns:gbp=\"urn:ietf:params:xml:ns:pidf:geopriv10:basicPolicy\">\n" +
                                "          <gbp:retention-expiry>2006-01-11T03:42:28+00:00\n" +
                                "          </gbp:retention-expiry>\n" +
                                "         </usage-rules>\n" +
                                "         <method>Wiremap</method>\n" +
                                "        </geopriv>\n" +
                                "       </status>\n" +
                                "       <timestamp>1970-01-01T00:00:10+00:00</timestamp>\n" +
                                "      </tuple>\n" +
                                "     </presence>\n" +
                                "    </locationResponse>",
                        FindLocationResult.createFoundResult(Collections.singletonList(new Location(-34.407, 150.88001, 30.0, Instant.ofEpochSecond(10))))),
                new TestParsingData("LocationUnknown",
                        "<?xml version=\"1.0\"?>\n" +
                                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"locationUnknown\">\n" +
                                "      <message xml:lang=\"en\">errorMessage</message>\n" +
                                "</error>",
                        FindLocationResult.createFailureResult(new FindLocationResult.Status(FindLocationResult.StatusCode.LOCATION_UNKNOWN, "errorMessage"))),
                new TestParsingData("CannotProvideLiType",
                        "<?xml version=\"1.0\"?>\n" +
                                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"cannotProvideLiType\">\n" +
                                "      <message xml:lang=\"de\">other language</message>\n" +
                                "      <message xml:lang=\"en\">errorMessage</message>\n" +
                                "</error>",
                        FindLocationResult.createFailureResult(new FindLocationResult.Status(FindLocationResult.StatusCode.CANNOT_PROVIDE_LI_TYPE, "errorMessage"))),
                new TestParsingData("GeneralLisError",
                        "<?xml version=\"1.0\"?>\n" +
                                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"generalLisError\">\n" +
                                "      <message xml:lang=\"be\">other language</message>\n" +
                                "      <message xml:lang=\"de\">even another language</message>\n" +
                                "</error>",
                        FindLocationResult.createFailureResult(new FindLocationResult.Status(FindLocationResult.StatusCode.GENERAL_LIS_ERROR, "other language"))),
                new TestParsingData("Timeout",
                        "<?xml version=\"1.0\"?>\n" +
                                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"timeout\">\n" +
                                "      <message xml:lang=\"en\">errorMessage</message>\n" +
                                "</error>",
                        FindLocationResult.createFailureResult(new FindLocationResult.Status(FindLocationResult.StatusCode.TIMEOUT, "errorMessage"))),
                new TestParsingData("NotLocatable",
                        "<?xml version=\"1.0\"?>\n" +
                                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"notLocatable\">\n" +
                                "      <message xml:lang=\"en\">errorMessage</message>\n" +
                                "</error>",
                        FindLocationResult.createFailureResult(new FindLocationResult.Status(FindLocationResult.StatusCode.NOT_LOCATABLE, "errorMessage"))),
                new TestParsingData("UnknownError",
                        "<?xml version=\"1.0\"?>\n" +
                                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"unknownError\">\n" +
                                "      <message xml:lang=\"en\">errorMessage</message>\n" +
                                "</error>",
                        FindLocationResult.createFailureResult(new FindLocationResult.Status(FindLocationResult.StatusCode.UNKNOWN_ERROR, "errorMessage"))),
                new TestParsingData("Multiple locations",
                        "<?xml version=\"1.0\"?>\n" +
                                "    <locationResponse xmlns=\"urn:ietf:params:xml:ns:geopriv:held\">\n" +
                                "     <presence xmlns=\"urn:ietf:params:xml:ns:pidf\"\n" +
                                "      entity=\"pres:3650n87934c@ls.example.com\">\n" +
                                "      <tuple id=\"b650sf789nd\">\n" +
                                "       <status>\n" +
                                "        <geopriv xmlns=\"urn:ietf:params:xml:ns:pidf:geopriv10\">\n" +
                                "         <location-info>\n" +
                                "           <gs:Circle xmlns:gs=\"http://www.opengis.net/pidflo/1.0\"\n" +
                                "               xmlns:gml=\"http://www.opengis.net/gml\"\n" +
                                "               srsName=\"urn:ogc:def:crs:EPSG::4326\">\n" +
                                "               <gml:pos>-34.407 150.88001</gml:pos>\n" +
                                "               <gs:radius uom=\"urn:ogc:def:uom:EPSG::9001\">30\n" +
                                "               </gs:radius>\n" +
                                "           </gs:Circle>" +
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
                                "       <timestamp>1970-01-01T00:00:10+00:00</timestamp>\n" +
                                "      </tuple>\n" +
                                "     </presence>\n" +
                                "    </locationResponse>",
                        FindLocationResult.createFoundResult(Arrays.asList(new Location(-34.407, 150.88001, 30.0, Instant.ofEpochSecond(10)), new Location(-34.407, 150.88001, 0.0, Instant.ofEpochSecond(10)))))
        );
    }

    static Stream<TestParsingWithExceptionData> testParsingWithExceptionData() {
        return Stream.of(
                new TestParsingWithExceptionData("requestError",
                        "<?xml version=\"1.0\"?>\n" +
                                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"requestError\">\n" +
                                "      <message xml:lang=\"en\">errorMessage</message>\n" +
                                "</error>",
                        new HeldException("requestError", "errorMessage")),
                new TestParsingWithExceptionData("xmlError",
                        "<?xml version=\"1.0\"?>\n" +
                                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"xmlError\">\n" +
                                "      <message xml:lang=\"de\">errorMessage</message>\n" +
                                "</error>",
                        new HeldException("xmlError", "errorMessage")),
                new TestParsingWithExceptionData("unsupportedMessage",
                        "<?xml version=\"1.0\"?>\n" +
                                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"unsupportedMessage\">\n" +
                                "      <message xml:lang=\"de\">other language</message>\n" +
                                "      <message xml:lang=\"en\">errorMessage</message>\n" +
                                "</error>",
                        new HeldException("unsupportedMessage", "errorMessage")
                ));
    }

    private static class TestParsingData {
        private final String description;
        private final String input;
        private final FindLocationResult expectedOutput;

        TestParsingData(String description, String input, FindLocationResult expectedOutput) {
            this.description = description;
            this.input = input;
            this.expectedOutput = expectedOutput;
        }

        String getInput() {
            return input;
        }

        FindLocationResult getExpectedOutput() {
            return expectedOutput;
        }

        @Override
        public String toString() {
            return "TestParsingData{" +
                    "description='" + description + '\'' +
                    ", input='" + input + '\'' +
                    ", expectedOutput=" + expectedOutput +
                    '}';
        }
    }

    private static class TestParsingWithExceptionData {
        private final String description;
        private final String input;
        private final Exception expectedException;

        TestParsingWithExceptionData(String description, String input, Exception expectedException) {
            this.description = description;
            this.input = input;
            this.expectedException = expectedException;
        }

        String getInput() {
            return input;
        }

        Exception getExpectedException() {
            return expectedException;
        }

        @Override
        public String toString() {
            return "TestParsingData{" +
                    "description='" + description + '\'' +
                    ", input='" + input + '\'' +
                    ", expectedException=" + expectedException +
                    '}';
        }
    }
}