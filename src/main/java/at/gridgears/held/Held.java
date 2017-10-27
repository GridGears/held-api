package at.gridgears.held;

import java.util.function.Consumer;

public interface Held {
      void findLocation(String identifier, Consumer<LocationResult> callback);
}
