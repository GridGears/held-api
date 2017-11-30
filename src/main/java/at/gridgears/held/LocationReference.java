package at.gridgears.held;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.net.URI;
import java.time.Instant;
import java.util.Objects;

public class LocationReference implements Serializable {
    private static final long serialVersionUID = 1L;

    private final URI uri;
    private final Instant expiration;

    public LocationReference(URI uri, Instant expiration) {
        this.uri = uri;
        this.expiration = expiration;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public boolean isExpired() {
        return expiration.isBefore(Instant.now());
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocationReference that = (LocationReference) o;
        return Objects.equals(uri, that.uri) &&
                Objects.equals(expiration, that.expiration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, expiration);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("uri", uri)
                .append("expiration", expiration)
                .toString();
    }
}
