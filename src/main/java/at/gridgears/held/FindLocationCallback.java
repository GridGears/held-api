package at.gridgears.held;

public interface FindLocationCallback {
    void success(LocationResult locationResult);

    void failed(Exception exception);
}
