package at.gridgears.held.internal;


import at.gridgears.held.Location;
import at.gridgears.held.FindLocationResult;
import at.gridgears.schemas.held.*;
import org.apache.commons.lang3.Validate;
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
    private static final String DEFAULT_LANGUAGE = "en";

    private final Jaxb2Marshaller jaxb2Marshaller;
    private final String language;

    ResponseParser(String language) {
        Validate.notEmpty(language, "language must not be null or empty");
        this.language = language;
        jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setSupportJaxbElementClass(true);
        jaxb2Marshaller.setCheckForXmlRootElement(false);
        jaxb2Marshaller.setClassesToBeBound(LocationResponseType.class, LocationTypeType.class, ErrorType.class);
    }

    FindLocationResult parse(String responseContent) throws ResponseParsingException, HeldException {
        Object unmarshalled = unmarshall(responseContent);

        Optional<LocationResponseType> locationResponseTypeOptional = getValue(unmarshalled, LocationResponseType.class);
        if (locationResponseTypeOptional.isPresent()) {
            return FindLocationResult.createFoundResult(parseLocationResult(locationResponseTypeOptional.get()));
        } else {
            Optional<ErrorType> errorTypeOptional = getValue(unmarshalled, ErrorType.class);
            if (errorTypeOptional.isPresent()) {
                return FindLocationResult.createFailureResult(parseErrorResult(errorTypeOptional.get()));
            } else {
                throw new ResponseParsingException("Could not parse HELD response. Invalid content");
            }
        }
    }


    private Object unmarshall(String responseContent) throws ResponseParsingException {
        try {
            return jaxb2Marshaller.unmarshal(new StreamSource(new StringReader(responseContent)));
        } catch (Exception e) {
            throw new ResponseParsingException("Could not unmarshall responseContent", e);
        }
    }

    private List<Location> parseLocationResult(LocationResponseType locationResponseType) throws ResponseParsingException {
        try {
            List<Location> resultLocations = new LinkedList<>();

            Optional<Presence> presenceOptional = getValue(locationResponseType.getAny(), Presence.class);
            presenceOptional.ifPresent(presence -> {
                List<Tuple> tuples = presence.getTuple();
                if (!tuples.isEmpty()) {
                    Tuple tuple = tuples.get(0);
                    Status status = tuple.getStatus();
                    if (status != null) {
                        List<Object> statuses = status.getAny();
                        if (!statuses.isEmpty()) {
                            Optional<Geopriv> geoprivOptional = getValue(statuses.get(0), Geopriv.class);
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


    private FindLocationResult.Status parseErrorResult(ErrorType errorType) throws HeldException {
        String message = getLocalizedMessage(errorType.getMessage());

        FindLocationResult.Status result;
        switch (errorType.getCode()) {
            case "requestError":
                throw new HeldException(errorType.getCode(), message);
            case "xmlError":
                throw new HeldException(errorType.getCode(), message);
            case "generalLisError":
                result = new FindLocationResult.Status(FindLocationResult.StatusCode.GENERAL_LIS_ERROR, message);
                break;
            case "locationUnknown":
                result = new FindLocationResult.Status(FindLocationResult.StatusCode.LOCATION_UNKNOWN, message);
                break;
            case "unsupportedMessage":
                throw new HeldException(errorType.getCode(), message);
            case "timeout":
                result = new FindLocationResult.Status(FindLocationResult.StatusCode.TIMEOUT, message);
                break;
            case "cannotProvideLiType":
                result = new FindLocationResult.Status(FindLocationResult.StatusCode.CANNOT_PROVIDE_LI_TYPE, message);
                break;
            case "notLocatable":
                result = new FindLocationResult.Status(FindLocationResult.StatusCode.NOT_LOCATABLE, message);
                break;
            default:
                result = new FindLocationResult.Status(FindLocationResult.StatusCode.UNKNOWN_ERROR, message);
                break;
        }
        return result;
    }

    private String getLocalizedMessage(List<ErrorMsgType> messages) {
        String result = getMessageWithLanguage(messages, language);
        if (result == null) {
            result = getMessageWithLanguage(messages, DEFAULT_LANGUAGE);
            if (result == null) {
                result = !messages.isEmpty() ? messages.get(0).getValue() : "";
            }
        }

        return result;
    }

    private String getMessageWithLanguage(List<ErrorMsgType> messages, String messageLanguage) {
        return messages.stream().filter(msg -> isLang(msg, messageLanguage)).findFirst().map(ErrorMsgType::getValue).orElse(null);
    }

    private boolean isLang(ErrorMsgType msg, String messageLanguage) {
        return msg.getOtherAttributes().entrySet().stream().anyMatch(entry -> entry.getKey().getLocalPart().equals("lang") && entry.getValue().equals(messageLanguage));
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

    @SuppressWarnings("unchecked")
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
