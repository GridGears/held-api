package at.gridgears.held;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ResponseParserTest {
    private static ResponseParser parser;

    @BeforeAll
    static void initParser() {
        parser = new ResponseParser();
    }

    @ParameterizedTest
    @MethodSource("testParsingData")
    void testParsing(TestData testData) throws Exception {
        assertThat("output", parser.parse(testData.getInput()), is(testData.getExpectedOutput()));
    }

    static Stream<TestData> testParsingData() {
        return Stream.of(
                new TestData("Point",
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
                        new LocationResult(Collections.singletonList(new Location(-34.407, 150.88001, 0.0, Instant.ofEpochSecond(10))))),
                new TestData("Circle",
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
                        new LocationResult(Collections.singletonList(new Location(-34.407, 150.88001, 30.0, Instant.ofEpochSecond(10)))))
        );
    }

    private static class TestData {
        private final String description;
        private final String input;
        private final LocationResult expectedOutput;

        public TestData(String description, String input, LocationResult expectedOutput) {
            this.description = description;
            this.input = input;
            this.expectedOutput = expectedOutput;
        }

        String getInput() {
            return input;
        }

        LocationResult getExpectedOutput() {
            return expectedOutput;
        }

        @Override
        public String toString() {
            return "TestData{" +
                    "description='" + description + '\'' +
                    ", input='" + input + '\'' +
                    ", expectedOutput=" + expectedOutput +
                    '}';
        }
    }
}