package at.gridgears.held;

import org.apache.http.client.methods.HttpPost;

public class NoAuthorization implements Authorization {
    @Override
    public void applyAuthorization(HttpPost httpPost) {
        //nothing to do
    }
}
