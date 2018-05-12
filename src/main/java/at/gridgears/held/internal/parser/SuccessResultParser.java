package at.gridgears.held.internal.parser;

import at.gridgears.held.Location;
import at.gridgears.held.LocationReference;
import at.gridgears.schemas.held.LocationResponseType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

class SuccessResultParser {
    private final LocationParser locationParser = new LocationParser();
    private final LocationReferenceParser locationReferenceParser = new LocationReferenceParser();

    Pair<List<Location>, List<LocationReference>> parse(LocationResponseType locationResponseType) throws ResponseParsingException {
        try {
            List<Location> resultLocations = locationParser.parse(locationResponseType);
            List<LocationReference> resultReferences = locationReferenceParser.parse(locationResponseType);

            return Pair.of(resultLocations, resultReferences);
        } catch (Exception e) {
            throw new ResponseParsingException("Error parsing LocationResponseType", e);
        }
    }
}
