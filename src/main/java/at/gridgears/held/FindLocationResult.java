package at.gridgears.held;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;

public final class FindLocationResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ERROR_LOCATION_UNKNOWN = "locationUnknown";

    private final FindLocationError error;
    private final List<Location> locations;
    private final List<LocationReference> locationReferences;
    private final List<CivicAddress> civicAddresses;
    private String rawRequest;
    private final String rawResponse;

    public enum Status {
        FOUND,
        NOT_FOUND,
        ERROR
    }

    private FindLocationResult(List<Location> locations, List<CivicAddress> civicAddresses, List<LocationReference> locationReferences, @Nullable FindLocationError error, final String rawResponse) {
        Validate.noNullElements(locations, "locations must not be null or contain null elements");
        Validate.noNullElements(civicAddresses, "civicAddresses must not be null or contain null elements");
        Validate.noNullElements(locationReferences, "locationReferences must not be null or contain null elements");

        this.error = error;
        this.locations = Collections.unmodifiableList(new ArrayList<>(locations));
        this.civicAddresses = Collections.unmodifiableList(new ArrayList<>(civicAddresses));
        this.locationReferences = Collections.unmodifiableList(new ArrayList<>(locationReferences));
        this.rawResponse = rawResponse;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawRequest(String rawRequest) {
        this.rawRequest = rawRequest;
    }

    public String getRawRequest() {
        return rawRequest;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<CivicAddress> getCivicAddresses() {
        return civicAddresses;
    }

    public List<LocationReference> getLocationReferences() {
        return locationReferences;
    }

    public Optional<FindLocationError> getError() {
        return Optional.ofNullable(error);
    }

    public Status getStatus() {
        Status result;
        if (!locations.isEmpty() || !civicAddresses.isEmpty() || !locationReferences.isEmpty()) {
            result = Status.FOUND;
        } else if (error.getCode().equals(ERROR_LOCATION_UNKNOWN)) {
            result = Status.NOT_FOUND;
        } else {
            result = Status.ERROR;
        }

        return result;
    }

    public static FindLocationResult createFailureResult(FindLocationError error, String rawResponse) {
        Validate.notNull(error, "error must not be null");
        return new FindLocationResult(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), error, rawResponse);
    }

    public static FindLocationResult createFoundResult(List<Location> locations, List<CivicAddress> civicAddresses, List<LocationReference> locationReferences, String rawResponse) {
        if (locations.isEmpty() && civicAddresses.isEmpty() && locationReferences.isEmpty()) {
            throw new IllegalArgumentException("locations, civicAddresses and locationReferences must not all be empty for a success result");
        }
        return new FindLocationResult(locations, civicAddresses, locationReferences, null, rawResponse);
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
        return Objects.equals(error, that.error) &&
                Objects.equals(locations, that.locations) &&
                Objects.equals(locationReferences, that.locationReferences) &&
                Objects.equals(civicAddresses, that.civicAddresses) &&
                Objects.equals(rawRequest, that.rawRequest) &&
                Objects.equals(rawResponse, that.rawResponse);
    }

    @Override
    public int hashCode() {

        return Objects.hash(error, locations, locationReferences, civicAddresses, rawRequest, rawResponse);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("error", error)
                .append("locations", locations)
                .append("locationReferences", locationReferences)
                .append("civicAddresses", civicAddresses)
                .append("rawRequest", rawRequest)
                .append("rawResponse", rawResponse)
                .toString();
    }
}
