package at.gridgears.held;

import java.util.Optional;

public class LocationResult {
    private static LocationResult ERROR_RESULT = new LocationResult(Status.ERROR,null);
    private static LocationResult NOT_FOUND_RESULT = new LocationResult(Status.NOT_FOUND,null);

    enum Status {
        FOUND,
        NOT_FOUND,
        ERROR
    }

    private final Status status;
    private final Location location;


    private LocationResult(Status status, Location location) {
        this.status = status;
        this.location = location;
    }


    public Optional<Location> getLocation() {
        return Optional.ofNullable(location);
    }

    public Status getStatus() {
        return this.status;
    }

    public static LocationResult createFoundResult(Location location) {
        return new LocationResult(Status.FOUND, location);
    }

    public static LocationResult createNotFoundResult() {
        return NOT_FOUND_RESULT;
    }

    public static LocationResult createErrorResult() {
        return ERROR_RESULT;
    }

}
