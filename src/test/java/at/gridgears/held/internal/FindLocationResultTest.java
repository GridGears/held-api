package at.gridgears.held.internal;

import at.gridgears.held.Location;
import at.gridgears.held.FindLocationResult;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class FindLocationResultTest {

    @Test
    void creatingErrorResultWithLocationFoundStatusThrowsException() {
        assertThrows(IllegalStateException.class, () -> FindLocationResult.createFailureResult(new FindLocationResult.Status(FindLocationResult.StatusCode.LOCATION_FOUND, "message")));
    }

    @Test
    void creatingFoundResultWithEmptyLocationsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> FindLocationResult.createFoundResult(Collections.emptyList()));
    }

    @Test
    void hasLocationReturnsTrueForFoundResult() {
        Location location = mock(Location.class);
        assertThat("has locations", FindLocationResult.createFoundResult(Collections.singletonList(location)).hasLocations(), is(true));
    }

    @Test
    void hasLocationReturnsFalseForErrorResult() {
        assertThat("has locations", FindLocationResult.createFailureResult(new FindLocationResult.Status(FindLocationResult.StatusCode.LOCATION_UNKNOWN, "message")).hasLocations(), is(false));
    }
}