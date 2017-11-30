package at.gridgears.held.internal.parser;

import at.gridgears.held.FindLocationError;
import at.gridgears.held.FindLocationResult;
import at.gridgears.held.Location;
import at.gridgears.held.LocationReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResponseParserTest {
    private static ResponseParser parser;

    @BeforeAll
    static void initParser() {
        parser = new ResponseParser("de");
    }

    @ParameterizedTest
    @MethodSource("testParsingData")
    void testParsing(TestParsingData testData) throws Exception {
        FindLocationResult result = parser.parse(testData.getInput());
        assertThat("output", result, is(testData.getExpectedOutput()));
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
                        FindLocationResult.createFoundResult(Collections.singletonList(new Location(-34.407, 150.88001, 0.0, Instant.ofEpochSecond(10))), Collections.emptyList())),
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
                        FindLocationResult.createFoundResult(Collections.singletonList(new Location(-34.407, 150.88001, 30.0, Instant.ofEpochSecond(10))), Collections.emptyList())),
                new TestParsingData("ErrorResponse",
                        "<?xml version=\"1.0\"?>\n" +
                                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"locationUnknown\">\n" +
                                "      <message xml:lang=\"en\">errorMessage</message>\n" +
                                "</error>",
                        FindLocationResult.createFailureResult(new FindLocationError("locationUnknown", "errorMessage"))),
                new TestParsingData("ErrorResponse: use preferred language",
                        "<?xml version=\"1.0\"?>\n" +
                                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"locationUnknown\">\n" +
                                "      <message xml:lang=\"en\">errorMessage</message>\n" +
                                "      <message xml:lang=\"de\">preferred language</message>\n" +
                                "</error>",
                        FindLocationResult.createFailureResult(new FindLocationError("locationUnknown", "preferred language"))),
                new TestParsingData("ErrorResponse: use default language",
                        "<?xml version=\"1.0\"?>\n" +
                                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"locationUnknown\">\n" +
                                "      <message xml:lang=\"be\">other language</message>\n" +
                                "      <message xml:lang=\"en\">errorMessage</message>\n" +
                                "      <message xml:lang=\"fr\">even another language</message>\n" +
                                "</error>",
                        FindLocationResult.createFailureResult(new FindLocationError("locationUnknown", "errorMessage"))),
                new TestParsingData("ErrorResponse: use any language",
                        "<?xml version=\"1.0\"?>\n" +
                                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"locationUnknown\">\n" +
                                "      <message xml:lang=\"be\">other language</message>\n" +
                                "      <message xml:lang=\"fr\">even another language</message>\n" +
                                "</error>",
                        FindLocationResult.createFailureResult(new FindLocationError("locationUnknown", "other language"))),
                new TestParsingData("Reference",
                        "<?xml version=\"1.0\"?>\n" +
                                "    <locationResponse xmlns=\"urn:ietf:params:xml:ns:geopriv:held\">\n" +
                                "      <locationUriSet expires=\"2006-01-01T13:00:00.0Z\">\n" +
                                "       <locationURI>https://ls.example.com:9768/357yc6s64ceyoiuy5ax3o\n" +
                                "       </locationURI>\n" +
                                "       <locationURI>sip:9769+357yc6s64ceyoiuy5ax3o@ls.example.com</locationURI>\n" +
                                "     </locationUriSet>\n" +
                                "   </locationResponse>",
                        FindLocationResult.createFoundResult(Collections.emptyList(), Arrays.asList(
                                new LocationReference(URI.create("https://ls.example.com:9768/357yc6s64ceyoiuy5ax3o"), Instant.parse("2006-01-01T13:00:00.0Z")),
                                new LocationReference(URI.create("sip:9769+357yc6s64ceyoiuy5ax3o@ls.example.com"), Instant.parse("2006-01-01T13:00:00.0Z"))
                        ))),
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
                        FindLocationResult.createFoundResult(Arrays.asList(new Location(-34.407, 150.88001, 30.0, Instant.ofEpochSecond(10)), new Location(-34.407, 150.88001, 0.0, Instant.ofEpochSecond(10))), Collections.emptyList()))
        );
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
}