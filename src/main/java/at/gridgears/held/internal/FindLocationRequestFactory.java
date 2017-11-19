package at.gridgears.held.internal;

import at.gridgears.held.FindLocationRequest;
import org.apache.commons.lang3.Validate;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

class FindLocationRequestFactory {
    private static final ContentType CONTENT_TYPE = ContentType.create("application/held+xml", new BasicNameValuePair("charset", "utf-8"));

    private final FindLocationRequestMarshaller findLocationRequestMarshaller = new FindLocationRequestMarshaller();
    private final List<Header> headers;

    public FindLocationRequestFactory(List<Header> headers) {
        Validate.noNullElements(headers, "headers must not be null or contain null elements");
        this.headers = new ArrayList<>(headers);
    }

    HttpPost createRequest(URI uri, FindLocationRequest request) {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader(new BasicHeader("Content-Type", "application/held+xml;charset=utf-8"));
        httpPost.setEntity(createEntity(request));

        headers.forEach(httpPost::addHeader);

        return httpPost;
    }

    private StringEntity createEntity(FindLocationRequest request) {
        return new StringEntity(findLocationRequestMarshaller.marshall(request), CONTENT_TYPE);
    }
}
