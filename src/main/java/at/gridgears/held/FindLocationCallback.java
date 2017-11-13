package at.gridgears.held;

public interface FindLocationCallback {
    void completed(LocationResult locationResult);

    void failed(String identifier, Exception exception);
}
