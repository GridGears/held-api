package at.gridgears.held;

public interface LocationRequestCallback {
    void success(LocationResult locationResult);

    void failed(Exception exception);
}
