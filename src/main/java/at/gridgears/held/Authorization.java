package at.gridgears.held;

import org.apache.http.client.methods.HttpPost;

interface Authorization {
    void applyAuthorization(HttpPost httpPost);
}
