package at.gridgears.held.internal.parser;

import at.gridgears.held.FindLocationError;
import at.gridgears.schemas.held.ErrorMsgType;
import at.gridgears.schemas.held.ErrorType;
import org.apache.commons.lang3.Validate;

import java.util.List;

class ErrorResultParser {
    private static final String DEFAULT_LANGUAGE = "en";
    private final String language;

    ErrorResultParser(String language) {
        this.language = language;
        Validate.notEmpty(language, "language must not be null or empty");
    }

    FindLocationError parse(ErrorType errorType) {
        return new FindLocationError(errorType.getCode(), getLocalizedMessage(errorType.getMessage()));
    }

    private String getLocalizedMessage(List<ErrorMsgType> messages) {
        String result = getMessageWithLanguage(messages, language);
        if (result == null) {
            result = getMessageWithLanguage(messages, DEFAULT_LANGUAGE);
            if (result == null) {
                result = !messages.isEmpty() ? messages.get(0).getValue() : "";
            }
        }

        return result;
    }

    private String getMessageWithLanguage(List<ErrorMsgType> messages, String messageLanguage) {
        return messages.stream().filter(msg -> isLang(msg, messageLanguage)).findFirst().map(ErrorMsgType::getValue).orElse(null);
    }

    private boolean isLang(ErrorMsgType msg, String messageLanguage) {
        return msg.getOtherAttributes().entrySet().stream().anyMatch(entry -> entry.getKey().getLocalPart().equals("lang") && entry.getValue().equals(messageLanguage));
    }
}
