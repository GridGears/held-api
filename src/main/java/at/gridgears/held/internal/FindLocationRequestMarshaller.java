package at.gridgears.held.internal;

import at.gridgears.held.FindLocationRequest;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.NotImplementedException;

@SuppressFBWarnings("VA_FORMAT_STRING_USES_NEWLINE")
class FindLocationRequestMarshaller {
    String marshall(FindLocationRequest request) {
        return String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<locationRequest xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" xmlns:ns2=\"urn:ietf:params:xml:ns:geopriv:held:id\"%s>\n" +
                "%s" +
                "%s" +
                "</locationRequest>", marshallResponseTime(request), marshallLocationTypes(request), marshallDevice(request));
    }

    private String marshallResponseTime(FindLocationRequest request) {
        return request.getResponseTime().map(responseTime -> String.format(" responseTime=\"%s\"", responseTime)).orElse("");
    }

    private String marshallDevice(FindLocationRequest request) {
        return String.format("   <ns2:device>\n" +
                "        <ns2:uri>%s</ns2:uri>\n" +
                "    </ns2:device>\n", request.getIdentifier());
    }

    private String marshallLocationTypes(FindLocationRequest request) {
        StringBuilder result = new StringBuilder(30);
        if (!request.getLocationTypes().isEmpty()) {
            result.append(String.format("   <locationType exact=\"%s\">\n", request.isExact()));
            request.getLocationTypes().forEach(locationType -> result.append(String.format("       %s\n", getLocationTypeString(locationType))));
            result.append("   </locationType>\n");
        }

        return result.toString();
    }

    private String getLocationTypeString(FindLocationRequest.LocationType locationType) {
        String result;
        switch (locationType) {
            case ANY:
                result = "any";
                break;
            case GEODETIC:
                result = "geodetic";
                break;
            case CIVIC:
                result = "civic";
                break;
            case LOCATION_URI:
                result = "locationURI";
                break;
            default:
                throw new NotImplementedException("Marshalling of locationType '" + locationType + "' not implemented");
        }

        return result;
    }
}
