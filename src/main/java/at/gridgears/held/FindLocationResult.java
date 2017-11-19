package at.gridgears.held;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FindLocationResult implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String ERROR_LOCATION_UNKNOWN = "locationUnknown";

    private final FindLocationError error;
    private final List<Location> locations;

    public enum Status {
        FOUND,
        NOT_FOUND,
        ERROR
    }

    private FindLocationResult(List<Location> locations, @Nullable FindLocationError error) {
        Validate.noNullElements(locations, "locations must not be null or contain null elements");

        this.error = error;
        this.locations = Collections.unmodifiableList(new ArrayList<>(locations));
    }

    public List<Location> getLocations() {
        return locations;
    }

    public Optional<FindLocationError> getError() {
        return Optional.ofNullable(error);
    }

    public Status getStatus() {
        Status result;
        if (!locations.isEmpty()) {
            result = Status.FOUND;
        } else if (error.getCode().equals(ERROR_LOCATION_UNKNOWN)) {
            result = Status.NOT_FOUND;
        } else {
            result = Status.ERROR;
        }

        return result;
    }

    public static FindLocationResult createFailureResult(FindLocationError error) {
        Validate.notNull(error, "error must not be null");
        return new FindLocationResult(Collections.emptyList(), error);
    }

    public static FindLocationResult createFoundResult(List<Location> locations) {
        Validate.notEmpty(locations, "locations must not be empty for a found result");
        return new FindLocationResult(locations, null);
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
                .append(error, that.error)
                .append(locations, that.locations)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(error)
                .append(locations)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("locations", locations)
                .append("status", error)
                .toString();
    }
}
