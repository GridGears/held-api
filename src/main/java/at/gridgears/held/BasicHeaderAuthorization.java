package at.gridgears.held;

import org.apache.commons.lang3.Validate;
import org.apache.http.client.methods.HttpPost;

class BasicHeaderAuthorization implements Authorization {
    private final String token;

    BasicHeaderAuthorization(String token) {
        Validate.notEmpty(token, "token must not be null or empty");
        this.token = token;
    }

    @Override
    public void applyAuthorization(HttpPost httpPost) {
        httpPost.addHeader("Authorization", "Bearer " + token);
    }
}
