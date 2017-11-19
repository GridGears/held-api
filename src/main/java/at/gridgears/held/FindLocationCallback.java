package at.gridgears.held;

public interface FindLocationCallback {
    void completed(FindLocationRequest request, FindLocationResult findLocationResult);

    void failed(FindLocationRequest request, Exception exception);
}
