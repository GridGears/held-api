package at.gridgears.held.internal.parser;

import at.gridgears.held.CivicAddress;
import at.gridgears.held.Location;
import at.gridgears.held.LocationReference;
import at.gridgears.schemas.held.LocationResponseType;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

class SuccessResultParser {
    private final LocationParser locationParser = new LocationParser();
    private final CivicAddressParser civicAddressParser = new CivicAddressParser();
    private final LocationReferenceParser locationReferenceParser = new LocationReferenceParser();

    Triple<List<Location>, List<CivicAddress>, List<LocationReference>> parse(LocationResponseType locationResponseType) throws ResponseParsingException {
        try {
            List<Location> resultLocations = locationParser.parse(locationResponseType);
            List<CivicAddress> resultCivicAddresses = civicAddressParser.parse(locationResponseType);
            List<LocationReference> resultReferences = locationReferenceParser.parse(locationResponseType);

            return Triple.of(resultLocations, resultCivicAddresses, resultReferences);
        } catch (Exception e) {
            throw new ResponseParsingException("Error parsing LocationResponseType", e);
        }
    }
}
