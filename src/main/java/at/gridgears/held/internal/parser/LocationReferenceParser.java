package at.gridgears.held.internal.parser;

import at.gridgears.held.LocationReference;
import at.gridgears.schemas.held.LocationResponseType;
import at.gridgears.schemas.held.ReturnLocationType;
import org.apache.commons.lang3.mutable.MutableObject;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static at.gridgears.held.internal.parser.ParseUtils.toInstant;

class LocationReferenceParser {
    List<LocationReference> parse(LocationResponseType locationResponseType) {
        MutableObject<Instant> expires = new MutableObject<>();

        List<LocationReference> result = new LinkedList<>();
        Optional.ofNullable(locationResponseType.getLocationUriSet())
                .map(storeExpires(expires))
                .map(ReturnLocationType::getLocationURI)
                .map(List::stream)
                .ifPresent(stream ->
                        stream.forEach(uri -> result.add(new LocationReference(URI.create(uri.trim()), expires.getValue())))
                );
        return result;
    }

    private Function<ReturnLocationType, ReturnLocationType> storeExpires(MutableObject<Instant> expires) {
        return returnLocationType -> {
            expires.setValue(toInstant(returnLocationType.getExpires()));
            return returnLocationType;
        };
    }
}
