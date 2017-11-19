package at.gridgears.held.internal;

import at.gridgears.held.Location;
import at.gridgears.held.LocationResult;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class LocationResultTest {

    @Test
    void creatingErrorResultWithLocationFoundStatusThrowsException() {
        assertThrows(IllegalStateException.class, () -> LocationResult.createFailureResult("identifier", new LocationResult.Status(LocationResult.StatusCode.LOCATION_FOUND, "message")));
    }

    @Test
    void creatingFoundResultWithEmptyLocationsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> LocationResult.createFoundResult("identifier", Collections.emptyList()));
    }

    @Test
    void hasLocationReturnsTrueForFoundResult() {
        Location location = mock(Location.class);
        assertThat("has locations", LocationResult.createFoundResult("identifier", Collections.singletonList(location)).hasLocations(), is(true));
    }

    @Test
    void hasLocationReturnsFalseForErrorResult() {
        assertThat("has locations", LocationResult.createFailureResult("identifier", new LocationResult.Status(LocationResult.StatusCode.LOCATION_UNKNOWN, "message")).hasLocations(), is(false));
    }
}