package at.gridgears.held.internal.parser;

import at.gridgears.held.Location;
import at.gridgears.held.LocationReference;
import at.gridgears.schemas.held.*;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URI;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

class SuccessResultParser {
    Pair<List<Location>, List<LocationReference>> parse(LocationResponseType locationResponseType) throws ResponseParsingException {
        try {
            List<Location> resultLocations = new LinkedList<>();
            List<LocationReference> resultReferences = new LinkedList<>();

            extractReferences(locationResponseType, resultReferences);
            extractLocations(locationResponseType, resultLocations);

            return Pair.of(resultLocations, resultReferences);
        } catch (Exception e) {
            throw new ResponseParsingException("Error parsing LocationResponseType", e);
        }
    }

    private void extractReferences(LocationResponseType locationResponseType, List<LocationReference> resultReferences) {
        MutableObject<Instant> expires = new MutableObject<>();

        Optional.ofNullable(locationResponseType.getLocationUriSet())
                .map(storeExpires(expires))
                .map(ReturnLocationType::getLocationURI)
                .map(List::stream)
                .ifPresent(stream ->
                    stream.forEach(uri -> resultReferences.add(new LocationReference(URI.create(uri.trim()), expires.getValue())))
                );
    }

    private void extractLocations(LocationResponseType locationResponseType, List<Location> resultLocations) {
        MutableObject<Instant> timestamp = new MutableObject<>();

        JaxbElementUtil.getValue(locationResponseType.getAny(), Presence.class)
            .map(Presence::getTuple)
            .map(SuccessResultParser::first)
            .map(storeTimestamp(timestamp))
            .map(Tuple::getStatus)
            .map(Status::getAny)
            .map(SuccessResultParser::first)
            .map(SuccessResultParser::getGeopriv)
            .map(Geopriv::getLocationInfo)
            .ifPresent(locInfoType ->
                   resultLocations.addAll(parseLocations(locInfoType.getAny(),timestamp.getValue()))
            );
    }

    private Function<ReturnLocationType, ReturnLocationType> storeExpires(MutableObject<Instant> expires) {
        return returnLocationType -> {
            expires.setValue(toInstant(returnLocationType.getExpires()));
            return returnLocationType;
        };
    }

    private Function<Tuple, Tuple> storeTimestamp(MutableObject<Instant> timestamp) {
        return tuple -> {
            timestamp.setValue(getTimestamp(tuple));
            return tuple;
        };
    }

    private static <T> T first(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    private static Geopriv getGeopriv(Object object) {
        return JaxbElementUtil.getValue(object, Geopriv.class).get();
    }

    private Instant getTimestamp(Tuple tuple) {
        XMLGregorianCalendar tupleTimestamp = tuple.getTimestamp();
        return toInstant(tupleTimestamp);
    }

    private Instant toInstant(XMLGregorianCalendar tupleTimestamp) {
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
