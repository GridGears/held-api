package at.gridgears.held;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FindLocationRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum LocationType {
        ANY,
        GEODETIC,
        CIVIC,
        LOCATION_URI
    }

    private final String identifier;
    private final String responseTime;
    private final List<LocationType> locationTypes;
    private final boolean exact;

    public FindLocationRequest(String identifier) {
        this(identifier, Collections.emptyList(), false, null);
    }

    public FindLocationRequest(String identifier, List<LocationType> locationTypes, boolean exact) {
        this(identifier, locationTypes, exact, null);
    }

    public FindLocationRequest(String identifier, List<LocationType> locationTypes, boolean exact, @Nullable ResponseTime responseTime) {
        Validate.notEmpty(identifier, "identifier must not be null or empty");
        Validate.notNull(locationTypes, "locationTypes must not be null");
        Validate.noNullElements(locationTypes, "locationTypes must not contain null values");

        this.identifier = identifier;
        this.responseTime = responseTime != null ? responseTime.getResponseTime() : null;
        this.locationTypes = locationTypes;
        this.exact = exact;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Optional<String> getResponseTime() {
        return Optional.ofNullable(responseTime);
    }

    public List<LocationType> getLocationTypes() {
        return locationTypes;
    }

    public boolean isExact() {
        return exact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FindLocationRequest request = (FindLocationRequest) o;
        return exact == request.exact &&
                Objects.equals(identifier, request.identifier) &&
                Objects.equals(responseTime, request.responseTime) &&
                Objects.equals(locationTypes, request.locationTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, responseTime, locationTypes, exact);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("identifier", identifier)
                .append("responseTime", responseTime)
                .append("locationTypes", locationTypes)
                .append("exact", exact)
                .toString();
    }

    public static class ResponseTime {
        private final String responseTime;

        private ResponseTime(String responseTime) {
            this.responseTime = responseTime;
        }

        public static ResponseTime createForDuration(Duration responseTime) {
            Validate.notNull(responseTime, "responseTime must not be null");
            if (responseTime.toMillis() < 0) {
                throw new IllegalArgumentException("responseTime must not be negative");
            }
            return new ResponseTime(String.valueOf(responseTime.toMillis()));
        }

        public static ResponseTime createForEmergencyRouting() {
            return new ResponseTime("emergencyRouting");
        }

        public static ResponseTime createForEmergencyDispatch() {
            return new ResponseTime("emergencyDispatch");
        }

        String getResponseTime() {
            return responseTime;
        }
    }
}
