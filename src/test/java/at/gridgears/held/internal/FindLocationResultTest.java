package at.gridgears.held.internal;

import at.gridgears.held.FindLocationError;
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
    void creatingFoundResultWithEmptyLocationsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> FindLocationResult.createFoundResult(Collections.emptyList()));
    }

    @Test
    void statusIsFoundForLocationResult() {
        Location location = mock(Location.class);
        assertThat("status", FindLocationResult.createFoundResult(Collections.singletonList(location)).getStatus(), is(FindLocationResult.Status.FOUND));
    }

    @Test
    void statusIsNotFoundForLocationUnknownResult() {
        assertThat("status", FindLocationResult.createFailureResult(new FindLocationError("locationUnknown", "message")).getStatus(), is(FindLocationResult.Status.NOT_FOUND));
    }

    @Test
    void statusIsErrorForErrorResult() {
        assertThat("status", FindLocationResult.createFailureResult(new FindLocationError("unsupportedMessage", "message")).getStatus(), is(FindLocationResult.Status.ERROR));
    }
}