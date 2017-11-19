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

public class FindLocationRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum LocationType {
        ANY,
        GEODETIC,
        CIVIC,
        LOCATION_URI
    }

    private final String identifier;
    private final Duration responseTime;
    private final List<LocationType> locationTypes;
    private final boolean exact;

    public FindLocationRequest(String identifier) {
        this(identifier, null, Collections.emptyList(), false);
    }

    public FindLocationRequest(String identifier, List<LocationType> locationTypes, boolean exact) {
        this(identifier, null, locationTypes, exact);
    }

    public FindLocationRequest(String identifier, @Nullable Duration responseTime, List<LocationType> locationTypes, boolean exact) {
        Validate.notEmpty(identifier, "identifier must not be null or empty");
        Validate.notNull(locationTypes, "locationTypes must not be null");
        Validate.noNullElements(locationTypes, "locationTypes must not contain null values");

        this.identifier = identifier;
        this.responseTime = responseTime;
        this.locationTypes = locationTypes;
        this.exact = exact;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Duration getResponseTime() {
        return responseTime;
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
}
