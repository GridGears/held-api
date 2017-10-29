package at.gridgears.held;

import at.gridgears.protocols.held.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

class ResponseParser {
    private final Jaxb2Marshaller jaxb2Marshaller;

    ResponseParser() {
        jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setSupportJaxbElementClass(true);
        jaxb2Marshaller.setCheckForXmlRootElement(false);
        jaxb2Marshaller.setClassesToBeBound(LocationResponseType.class, LocationTypeType.class);
    }

    LocationResult parse(String responseContent) throws ResponseParsingException {
        Object unmarshalled = unmarshall(responseContent);

        Optional<LocationResponseType> locationResponseTypeOptional = getValue(unmarshalled, LocationResponseType.class);
        if (locationResponseTypeOptional.isPresent()) {
            return getLocationResult(locationResponseTypeOptional.get());
        } else {
            throw new ResponseParsingException("Could not parse HELD response: LocationResponse not found");
        }
    }

    private Object unmarshall(String responseContent) throws ResponseParsingException {
        Object unmarshalled;
        try {
            unmarshalled = jaxb2Marshaller.unmarshal(new StreamSource(new StringReader(responseContent)));
        } catch (Exception e) {
            throw new ResponseParsingException("Could not unmarshall responseContent", e);
        }
        return unmarshalled;
    }

    private LocationResult getLocationResult(LocationResponseType locationResponseType) throws ResponseParsingException {
        try {
            List<Location> resultLocations = new LinkedList<>();

            Optional<Presence> presenceOptional = getValue(locationResponseType.getAny(), Presence.class);
            presenceOptional.ifPresent(presence -> {
                List<Tuple> tuple = presence.getTuple();
                if (!tuple.isEmpty()) {
                    Tuple tuple1 = tuple.get(0);
                    Status status = tuple1.getStatus();
                    if (status != null) {
                        List<Object> statuses = status.getAny();
                        if (!statuses.isEmpty()) {
                            Optional<Geopriv> geoprivOptional = getValue(statuses.get(0), Geopriv.class);
                            geoprivOptional.ifPresent(geoPriv -> {
                                LocInfoType locationInfo = geoPriv.getLocationInfo();
                                if (locationInfo != null) {
                                    Instant timestamp = getTimestamp(tuple1);
                                    resultLocations.addAll(parseLocations(locationInfo.getAny(), timestamp));
                                }
                            });
                        }
                    }
                }
            });
            return new LocationResult(resultLocations);
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
            Optional<PointType> pointTypeOptional = getValue(location, PointType.class);
            if (pointTypeOptional.isPresent()) {
                pointTypeOptional.ifPresent(point -> result.add(getLocation(timestamp, point)));
            } else {
                Optional<CircleType> circleTypeOptional = getValue(location, CircleType.class);
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

    private <T> Optional<T> getValue(Object element, Class<T> expectedClass) {
        T result = null;
        if (element instanceof JAXBElement) {
            Object value = ((JAXBElement) element).getValue();
            if (expectedClass.isAssignableFrom(value.getClass())) {
                result = (T) value;
            }
        }

        return Optional.ofNullable(result);
    }

    private <T> Optional<T> getValue(List<Object> elements, Class<T> expectedClass) {
        T result = null;
        for (Object element : elements) {
            Optional<T> optional = getValue(element, expectedClass);
            if (optional.isPresent()) {
                result = optional.get();
                break;
            }
        }

        return Optional.ofNullable(result);
    }
}
