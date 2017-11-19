package at.gridgears.held.internal.parser;

import at.gridgears.held.Location;
import at.gridgears.schemas.held.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

class SuccessResultParser {
    List<Location> parse(LocationResponseType locationResponseType) throws ResponseParsingException {
        try {
            List<Location> resultLocations = new LinkedList<>();

            Optional<Presence> presenceOptional = JaxbElementUtil.getValue(locationResponseType.getAny(), Presence.class);
            presenceOptional.ifPresent(presence -> {
                List<Tuple> tuples = presence.getTuple();
                if (!tuples.isEmpty()) {
                    Tuple tuple = tuples.get(0);
                    Status status = tuple.getStatus();
                    if (status != null) {
                        List<Object> statuses = status.getAny();
                        if (!statuses.isEmpty()) {
                            Optional<Geopriv> geoprivOptional = JaxbElementUtil.getValue(statuses.get(0), Geopriv.class);
                            geoprivOptional.ifPresent(geoPriv -> {
                                LocInfoType locationInfo = geoPriv.getLocationInfo();
                                if (locationInfo != null) {
                                    Instant timestamp = getTimestamp(tuple);
                                    resultLocations.addAll(parseLocations(locationInfo.getAny(), timestamp));
                                }
                            });
                        }
                    }
                }
            });
            return resultLocations;
        } catch (Exception e) {
            throw new ResponseParsingException("Error parsing LocationResponseType", e);
        }
    }

    private Instant getTimestamp(Tuple tuple) {
        XMLGregorianCalendar tupleTimestamp = tuple.getTimestamp();
        return tupleTimestamp.toGregorianCalendar().getTime().toInstant();
    }


    private List<Location> parseLocations(List<Object> locations, Instant timestamp) {
        List<Location> result = new LinkedList<>();
        locations.forEach(location -> {
            Optional<PointType> pointTypeOptional = JaxbElementUtil.getValue(location, PointType.class);
            if (pointTypeOptional.isPresent()) {
                pointTypeOptional.ifPresent(point -> result.add(getLocation(timestamp, point)));
            } else {
                Optional<CircleType> circleTypeOptional = JaxbElementUtil.getValue(location, CircleType.class);
                circleTypeOptional.ifPresent(circle -> result.add(getLocation(timestamp, circle)));
            }
        });
        return result;
    }

    private Location getLocation(Instant timestamp, PointType point) {
        List<String> coordinates = point.getPos().getValue();
        return new Location(Double.valueOf(coordinates.get(0)), Double.valueOf(coordinates.get(1)), 0.0, timestamp);
    }

    private Location getLocation(Instant timestamp, CircleType circle) {
        List<String> coordinates = circle.getPos().getValue();
        return new Location(Double.valueOf(coordinates.get(0)), Double.valueOf(coordinates.get(1)), circle.getRadius().getValue(), timestamp);
    }


}
