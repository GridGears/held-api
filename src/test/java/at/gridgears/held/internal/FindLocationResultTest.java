package at.gridgears.held.internal;

import at.gridgears.held.FindLocationError;
import at.gridgears.held.Location;
import at.gridgears.held.FindLocationResult;
import at.gridgears.held.LocationReference;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class FindLocationResultTest {

    private static final String RESPONSE_CONTENT = "responseContent";

    @Test
    void creatingFoundResultWithEmptyLocationsAndEmptyReferencesThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> FindLocationResult.createFoundResult(Collections.emptyList(), Collections.emptyList(), RESPONSE_CONTENT));
    }

    @Test
    void statusIsFoundForLocationResult() {
        Location location = mock(Location.class);
        assertThat("status", FindLocationResult.createFoundResult(Collections.singletonList(location), Collections.emptyList(), RESPONSE_CONTENT).getStatus(), is(FindLocationResult.Status.FOUND));
    }

    @Test
    void statusIsFoundForLocationReferenceResult() {
        assertThat("status", FindLocationResult.createFoundResult(Collections.emptyList(), Collections.singletonList(new LocationReference(URI.create("http://example.com/1234"), Instant.now())), RESPONSE_CONTENT).getStatus(), is(FindLocationResult.Status.FOUND));
    }

    @Test
    void statusIsNotFoundForLocationUnknownResult() {
        assertThat("status", FindLocationResult.createFailureResult(new FindLocationError("locationUnknown", "message"), RESPONSE_CONTENT).getStatus(), is(FindLocationResult.Status.NOT_FOUND));
    }

    @Test
    void statusIsErrorForErrorResult() {
        assertThat("status", FindLocationResult.createFailureResult(new FindLocationError("unsupportedMessage", "message"), RESPONSE_CONTENT).getStatus(), is(FindLocationResult.Status.ERROR));
    }
}