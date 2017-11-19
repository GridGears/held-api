package at.gridgears.held.internal.parser;


import at.gridgears.held.FindLocationResult;
import at.gridgears.schemas.held.ErrorType;
import at.gridgears.schemas.held.LocationResponseType;
import at.gridgears.schemas.held.LocationTypeType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.Optional;

public class ResponseParser {
    private final Jaxb2Marshaller jaxb2Marshaller;
    private final SuccessResultParser successResultParser;
    private final ErrorResultParser errorResultParser;

    public ResponseParser(String language) {
        jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setSupportJaxbElementClass(true);
        jaxb2Marshaller.setCheckForXmlRootElement(false);
        jaxb2Marshaller.setClassesToBeBound(LocationResponseType.class, LocationTypeType.class, ErrorType.class);

        this.successResultParser = new SuccessResultParser();
        this.errorResultParser = new ErrorResultParser(language);
    }

    public FindLocationResult parse(String responseContent) throws ResponseParsingException {
        Object unmarshalled = unmarshall(responseContent);

        Optional<LocationResponseType> locationResponseTypeOptional = JaxbElementUtil.getValue(unmarshalled, LocationResponseType.class);
        if (locationResponseTypeOptional.isPresent()) {
            return FindLocationResult.createFoundResult(successResultParser.parse(locationResponseTypeOptional.get()));
        } else {
            Optional<ErrorType> errorTypeOptional = JaxbElementUtil.getValue(unmarshalled, ErrorType.class);
            if (errorTypeOptional.isPresent()) {
                return FindLocationResult.createFailureResult(errorResultParser.parse(errorTypeOptional.get()));
            } else {
                throw new ResponseParsingException("Could not parse HELD response. Invalid content");
            }
        }
    }

    private Object unmarshall(String responseContent) throws ResponseParsingException {
        try {
            return jaxb2Marshaller.unmarshal(new StreamSource(new StringReader(responseContent)));
        } catch (Exception e) {
            throw new ResponseParsingException("Could not unmarshall responseContent", e);
        }
    }
}
