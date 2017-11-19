package at.gridgears.held.internal;

import at.gridgears.held.FindLocationRequest;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

class LocationRequestFactory {
    private static final ContentType CONTENT_TYPE = ContentType.create("application/held+xml", new BasicNameValuePair("charset", "utf-8"));
    private final List<Header> headers;

    public LocationRequestFactory(List<Header> headers) {
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
        return new StringEntity(createLocationRequest(request), CONTENT_TYPE);
    }

    @SuppressFBWarnings("VA_FORMAT_STRING_USES_NEWLINE")
    private String createLocationRequest(FindLocationRequest request) {

        return String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<locationRequest xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" xmlns:ns2=\"urn:ietf:params:xml:ns:geopriv:held:id\">\n" +
                "   <locationType exact=\"true\">geodetic</locationType>" +
                "   <ns2:device>\n" +
                "        <ns2:uri>%s</ns2:uri>\n" +
                "    </ns2:device>\n" +
                "</locationRequest>", request.getIdentifier());
    }
}