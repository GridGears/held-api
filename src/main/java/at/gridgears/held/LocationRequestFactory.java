package at.gridgears.held;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.net.URI;

class LocationRequestFactory {
    private static final ContentType CONTENT_TYPE = ContentType.create("application/held+xml", new BasicNameValuePair("charset", "utf-8"));

    HttpPost createRequest(URI uri, String identifier) {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader(new BasicHeader("Content-Type", "application/held+xml;charset=utf-8"));
        httpPost.setEntity(createEntity(identifier));

        return httpPost;
    }

    private StringEntity createEntity(String identifier) {
        return new StringEntity(createLocationRequest(identifier), CONTENT_TYPE);
    }

    @SuppressFBWarnings("VA_FORMAT_STRING_USES_NEWLINE")
    private String createLocationRequest(String identifier) {

        return String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<locationRequest xmlns=\"urn:ietf:params:xml:ns:geopriv:held\" xmlns:ns2=\"urn:ietf:params:xml:ns:geopriv:held:id\">\n" +
                "   <locationType exact=\"true\">geodetic</locationType>" +
                "   <ns2:device>\n" +
                "        <ns2:uri>%s</ns2:uri>\n" +
                "    </ns2:device>\n" +
                "</locationRequest>", identifier);
    }
}
