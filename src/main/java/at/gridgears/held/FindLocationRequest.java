package at.gridgears.held;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
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
        this(identifier, Collections.emptyList(), null, false);
    }

    public FindLocationRequest(String identifier, List<LocationType> locationTypes, boolean exact) {
        this(identifier, locationTypes, null, exact);
    }

    public FindLocationRequest(String identifier, List<LocationType> locationTypes, @Nullable ResponseTime responseTime, boolean exact) {
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

        FindLocationRequest that = (FindLocationRequest) o;

        return new EqualsBuilder()
                .append(exact, that.exact)
                .append(identifier, that.identifier)
                .append(responseTime, that.responseTime)
                .append(locationTypes, that.locationTypes)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(identifier)
                .append(responseTime)
                .append(locationTypes)
                .append(exact)
                .toHashCode();
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

        public String getResponseTime() {
            return responseTime;
        }
    }
}
