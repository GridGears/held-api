package at.gridgears.held.internal.parser;

import at.gridgears.held.*;
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
        String pointResponse = "<?xml version=\"1.0\"?>\n" +
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
                "    </locationResponse>";
        String cicleResponse = "<?xml version=\"1.0\"?>\n" +
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
                "    </locationResponse>";
        String errorResponse = "<?xml version=\"1.0\"?>\n" +
                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"locationUnknown\">\n" +
                "      <message xml:lang=\"en\">errorMessage</message>\n" +
                "</error>";
        String errorMessagePreferredLanguage = "<?xml version=\"1.0\"?>\n" +
                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"locationUnknown\">\n" +
                "      <message xml:lang=\"en\">errorMessage</message>\n" +
                "      <message xml:lang=\"de\">preferred language</message>\n" +
                "</error>";
        String errorMessageDefaultLanguage = "<?xml version=\"1.0\"?>\n" +
                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"locationUnknown\">\n" +
                "      <message xml:lang=\"be\">other language</message>\n" +
                "      <message xml:lang=\"en\">errorMessage</message>\n" +
                "      <message xml:lang=\"fr\">even another language</message>\n" +
                "</error>";
        String errorMessageAnyLanguage = "<?xml version=\"1.0\"?>\n" +
                "<error xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" code=\"locationUnknown\">\n" +
                "      <message xml:lang=\"be\">other language</message>\n" +
                "      <message xml:lang=\"fr\">even another language</message>\n" +
                "</error>";
        String reference = "<?xml version=\"1.0\"?>\n" +
                "    <locationResponse xmlns=\"urn:ietf:params:xml:ns:geopriv:held\">\n" +
                "      <locationUriSet expires=\"2006-01-01T13:00:00.0Z\">\n" +
                "       <locationURI>https://ls.example.com:9768/357yc6s64ceyoiuy5ax3o\n" +
                "       </locationURI>\n" +
                "       <locationURI>sip:9769+357yc6s64ceyoiuy5ax3o@ls.example.com</locationURI>\n" +
                "     </locationUriSet>\n" +
                "   </locationResponse>";
        String amlData = "<?xml version=\"1.0\"?>\n" +
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
                "         <aml xmlns=\"urn:ietf:params:xml:ns:gridgears:aml\"\n" +
                "                         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "                         xsi:schemaLocation=\"urn:ietf:params:xml:ns:gridgears:aml ../main/xsd/held/aml.xsd\">\n" +
                "                        <latitude>-34.407</latitude>\n" +
                "                        <longitude>150.88001</longitude>\n" +
                "                        <radius>12</radius>\n" +
                "                        <timestamp>1970-01-01T00:00:10+00:00</timestamp>\n" +
                "                        <confidenceLevel>87</confidenceLevel>\n" +
                "                        <positioningMethod>GNSS</positioningMethod>\n" +
                "                        <imsi>234302543446355</imsi>\n" +
                "                        <imei>356708041746734</imei>\n" +
                "                        <mcc>234</mcc>\n" +
                "                        <mnc>30</mnc>\n" +
                "                    </aml>\n" +
                "        </geopriv>\n" +
                "       </status>\n" +
                "       <timestamp>1970-01-01T00:00:10+00:00</timestamp>\n" +
                "      </tuple>\n" +
                "     </presence>\n" +
                "    </locationResponse>";
        String multipleLocations = "<?xml version=\"1.0\"?>\n" +
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
                "    </locationResponse>";
        String amlDataUnparsablePositioningMethod = "<?xml version=\"1.0\"?>\n" +
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
                "         <aml xmlns=\"urn:ietf:params:xml:ns:gridgears:aml\"\n" +
                "                         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "                         xsi:schemaLocation=\"urn:ietf:params:xml:ns:gridgears:aml ../main/xsd/held/aml.xsd\">\n" +
                "                        <latitude>-34.407</latitude>\n" +
                "                        <longitude>150.88001</longitude>\n" +
                "                        <radius>12</radius>\n" +
                "                        <timestamp>1970-01-01T00:00:10+00:00</timestamp>\n" +
                "                        <confidenceLevel>87</confidenceLevel>\n" +
                "                        <positioningMethod>unparsable</positioningMethod>\n" +
                "                        <imsi>234302543446355</imsi>\n" +
                "                        <imei>356708041746734</imei>\n" +
                "                        <mcc>234</mcc>\n" +
                "                        <mnc>30</mnc>\n" +
                "                    </aml>\n" +
                "        </geopriv>\n" +
                "       </status>\n" +
                "       <timestamp>1970-01-01T00:00:10+00:00</timestamp>\n" +
                "      </tuple>\n" +
                "     </presence>\n" +
                "    </locationResponse>";
        String civic = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<locationResponse xmlns=\"urn:ietf:params:xml:ns:geopriv:held\">\n" +
                "    <presence entity=\"pres:1234567890\" xmlns=\"urn:ietf:params:xml:ns:pidf\">\n" +
                "        <tuple id=\"12jkaejlk\">\n" +
                "            <status>\n" +
                "                <geopriv xmlns=\"urn:ietf:params:xml:ns:pidf:geopriv10\">\n" +
                "                    <location-info>\n" +
                "                        <civicAddress xmlns=\"urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr\">\n" +
                "                            <country>AU</country>\n" +
                "                            <A1>NSW</A1>\n" +
                "                            <A3>Wollongong</A3>\n" +
                "                            <A4>Gwynneville</A4>\n" +
                "                            <STS>Northfield Avenue</STS>\n" +
                "                            <LMK>University of Wollongong</LMK>\n" +
                "                            <FLR>2</FLR>\n" +
                "                            <NAM>Andrew Corporation</NAM>\n" +
                "                            <PC>2500</PC>\n" +
                "                            <BLD>39</BLD>\n" +
                "                            <SEAT>WS-183</SEAT>\n" +
                "                            <POBOX>U40</POBOX>\n" +
                "                        </civicAddress>\n" +
                "                    </location-info>\n" +
                "                    <usage-rules/>\n" +
                "                    <method>GNSS</method>\n" +
                "                </geopriv>\n" +
                "            </status>\n" +
                "            <timestamp>1970-01-01T01:00:10+00:00</timestamp>\n" +
                "        </tuple>\n" +
                "    </presence>\n" +
                "</locationResponse>\n";
        CivicAddress civicAddress = CivicAddress.CivicAddressBuilder.builder()
                .withCountry("AU")
                .withA1("NSW")
                .withA3("Wollongong")
                .withA4("Gwynneville")
                .withSts("Northfield Avenue")
                .withLmk("University of Wollongong")
                .withFlr("2")
                .withNam("Andrew Corporation")
                .withPc("2500")
                .withBld("39")
                .withSeat("WS-183")
                .withPobox("U40")
                .build();
        return Stream.of(
                new TestParsingData("Point",
                        pointResponse,
                        FindLocationResult.createFoundResult(Collections.singletonList(new Location(-34.407, 150.88001, 0.0, Instant.ofEpochSecond(10))), Collections.emptyList(), Collections.emptyList(), pointResponse)),
                new TestParsingData("Circle",
                        cicleResponse,
                        FindLocationResult.createFoundResult(Collections.singletonList(new Location(-34.407, 150.88001, 30.0, Instant.ofEpochSecond(10))), Collections.emptyList(), Collections.emptyList(), cicleResponse)),
                new TestParsingData("ErrorResponse",
                        errorResponse,
                        FindLocationResult.createFailureResult(new FindLocationError("locationUnknown", "errorMessage"), errorResponse)),
                new TestParsingData("ErrorResponse: use preferred language",
                        errorMessagePreferredLanguage,
                        FindLocationResult.createFailureResult(new FindLocationError("locationUnknown", "preferred language"), errorMessagePreferredLanguage)),
                new TestParsingData("ErrorResponse: use default language",
                        errorMessageDefaultLanguage,
                        FindLocationResult.createFailureResult(new FindLocationError("locationUnknown", "errorMessage"), errorMessageDefaultLanguage)),
                new TestParsingData("ErrorResponse: use any language",
                        errorMessageAnyLanguage,
                        FindLocationResult.createFailureResult(new FindLocationError("locationUnknown", "other language"), errorMessageAnyLanguage)),
                new TestParsingData("Reference",
                        reference,
                        FindLocationResult.createFoundResult(Collections.emptyList(), Collections.emptyList(), Arrays.asList(
                                new LocationReference(URI.create("https://ls.example.com:9768/357yc6s64ceyoiuy5ax3o"), Instant.parse("2006-01-01T13:00:00.0Z")),
                                new LocationReference(URI.create("sip:9769+357yc6s64ceyoiuy5ax3o@ls.example.com"), Instant.parse("2006-01-01T13:00:00.0Z"))
                        ), reference)),
                new TestParsingData("ResultWithAmlData",
                        amlData,
                        FindLocationResult.createFoundResult(Collections.singletonList(new Location(-34.407, 150.88001, 0.0, Instant.ofEpochSecond(10),
                                        new AmlData(-34.407, 150.88001, 12, Instant.ofEpochSecond(10), 87, PositioningMethod.GNSS, "234302543446355", "356708041746734", "234", "30"))),
                                Collections.emptyList(), Collections.emptyList(), amlData)),
                new TestParsingData("Multiple locations",
                        multipleLocations,
                        FindLocationResult.createFoundResult(Arrays.asList(new Location(-34.407, 150.88001, 30.0, Instant.ofEpochSecond(10)), new Location(-34.407, 150.88001, 0.0, Instant.ofEpochSecond(10))), Collections.emptyList(), Collections.emptyList(), multipleLocations)),
                new TestParsingData("ResultWithAmlDataAndUnparsablePositioningMethod",
                        amlDataUnparsablePositioningMethod,
                        FindLocationResult.createFoundResult(Collections.singletonList(new Location(-34.407, 150.88001, 0.0, Instant.ofEpochSecond(10),
                                        new AmlData(-34.407, 150.88001, 12, Instant.ofEpochSecond(10), 87, PositioningMethod.UNKNOWN, "234302543446355", "356708041746734", "234", "30"))),
                                Collections.emptyList(), Collections.emptyList(), amlDataUnparsablePositioningMethod)),
                new TestParsingData("ResultWithCivicLocation",
                        civic,
                        FindLocationResult.createFoundResult(Collections.emptyList(), Collections.singletonList(civicAddress), Collections.emptyList(), civic))
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