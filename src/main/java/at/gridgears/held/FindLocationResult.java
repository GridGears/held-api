package at.gridgears.held;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.*;

public class FindLocationResult implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum StatusCode {
        LOCATION_FOUND,
        GENERAL_LIS_ERROR,
        LOCATION_UNKNOWN,
        TIMEOUT,
        CANNOT_PROVIDE_LI_TYPE,
        NOT_LOCATABLE,
        UNKNOWN_ERROR
    }

    private final Status status;
    private final List<Location> locations;

    private FindLocationResult(Status status, List<Location> locations) {
        Validate.notNull(status, "status must not be null");
        Validate.noNullElements(locations, "locations must not be null or contain null elements");

        this.status = status;
        this.locations = Collections.unmodifiableList(new ArrayList<>(locations));
    }

    public List<Location> getLocations() {
        return locations;
    }

    public Status getStatus() {
        return status;
    }

    public boolean hasLocations() {
        return status.getStatusCode() == StatusCode.LOCATION_FOUND;
    }

    public static FindLocationResult createFailureResult(Status status) {
        Validate.notNull(status, "status must not be null");
        Validate.validState(status.getStatusCode() != StatusCode.LOCATION_FOUND, "Status must not be LOCATION_FOUND for an error result");
        return new FindLocationResult(status, Collections.emptyList());
    }

    public static FindLocationResult createFoundResult(List<Location> locations) {
        Validate.notEmpty(locations, "locations must not be empty for a LOCATION_FOUND result");
        return new FindLocationResult(new Status(StatusCode.LOCATION_FOUND, ""), locations);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FindLocationResult that = (FindLocationResult) o;

        return new EqualsBuilder()
                .append(status, that.status)
                .append(locations, that.locations)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(status)
                .append(locations)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("status", status)
                .append("locations", locations)
                .toString();
    }

    public static class Status implements Serializable {
        private static final long serialVersionUID = 1L;

        private final StatusCode statusCode;
        private final String message;

        public Status(StatusCode statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public StatusCode getStatusCode() {
            return statusCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Status status = (Status) o;

            return new EqualsBuilder()
                    .append(statusCode, status.statusCode)
                    .append(message, status.message)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(statusCode)
                    .append(message)
                    .toHashCode();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("statusCode", statusCode)
                    .append("message", message)
                    .toString();
        }
    }
}
