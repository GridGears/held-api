package at.gridgears.held.internal.parser;

import at.gridgears.held.AmlData;
import at.gridgears.held.Location;
import at.gridgears.schemas.held.*;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static at.gridgears.held.internal.parser.ParseUtils.getValue;
import static at.gridgears.held.internal.parser.ParseUtils.toInstant;

final class LocationParser {
    private final AmlDataParser amlDataParser = new AmlDataParser();

    List<Location> parse(LocationResponseType locationResponseType) {
        MutableObject<Instant> timestamp = new MutableObject<>();
        MutableObject<AmlData> amlData = new MutableObject<>();

        List<Location> result = new LinkedList<>();

        ParseUtils.getValue(locationResponseType.getAny(), Presence.class)
                .map(Presence::getTuple)
                .map(ParseUtils::first)
                .map(storeTimestamp(timestamp))
                .map(storeAml(amlData))
                .map(Tuple::getStatus)
                .map(Status::getAny)
                .map(ParseUtils::first)
                .map(LocationParser::getGeopriv)
                .ifPresent(geopriv -> result.addAll(parseLocation(geopriv, timestamp.getValue(), amlData.getValue())));
        return result;
    }

    private List<Location> parseLocation(Geopriv geopriv, Instant timestamp, AmlData amlData) {
        List<Location> result = new LinkedList<>();

        LocInfoType locationInfo = geopriv.getLocationInfo();
        if (locationInfo != null) {
            result.addAll(parseLocations(locationInfo.getAny(), timestamp, amlData));
        }

        return result;
    }

    private List<Location> parseLocations(List<Object> locations, Instant timestamp, @Nullable AmlData amlData) {
        List<Location> result = new LinkedList<>();
        locations.forEach(location -> {
            Optional<PointType> pointTypeOptional = ParseUtils.getValue(location, PointType.class);
            if (pointTypeOptional.isPresent()) {
                pointTypeOptional.ifPresent(point -> result.add(getLocation(timestamp, amlData, point)));
            } else {
                Optional<CircleType> circleTypeOptional = ParseUtils.getValue(location, CircleType.class);
                circleTypeOptional.ifPresent(circle -> result.add(getLocation(timestamp, amlData, circle)));
            }
        });
        return result;
    }

    private Function<Tuple, Tuple> storeTimestamp(MutableObject<Instant> timestamp) {
        return tuple -> {
            timestamp.setValue(getTimestamp(tuple));
            return tuple;
        };
    }

    private Function<Tuple, Tuple> storeAml(MutableObject<AmlData> amlData) {
        return tuple -> {
            Optional<AmlType> amlType = getValue(tuple.getAny(), AmlType.class);
            amlType.ifPresent(aml -> amlData.setValue(amlDataParser.parseAmlData(aml)));
            return tuple;
        };
    }

    private Instant getTimestamp(Tuple tuple) {
        XMLGregorianCalendar tupleTimestamp = tuple.getTimestamp();
        return toInstant(tupleTimestamp);
    }

    private static Geopriv getGeopriv(Object object) {
        return ParseUtils.getValue(object, Geopriv.class).get();
    }

    private Location getLocation(Instant timestamp, @Nullable AmlData amlData, PointType point) {
        List<String> coordinates = point.getPos().getValue();
        return new Location(Double.valueOf(coordinates.get(0)), Double.valueOf(coordinates.get(1)), 0.0, timestamp, amlData);
    }

    private Location getLocation(Instant timestamp, @Nullable AmlData amlData, CircleType circle) {
        List<String> coordinates = circle.getPos().getValue();
        return new Location(Double.valueOf(coordinates.get(0)), Double.valueOf(coordinates.get(1)), circle.getRadius().getValue(), timestamp, amlData);
    }

}
