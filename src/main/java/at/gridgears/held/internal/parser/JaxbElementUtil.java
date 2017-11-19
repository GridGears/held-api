package at.gridgears.held.internal.parser;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.Optional;

final class JaxbElementUtil {
    private JaxbElementUtil() {
        //must not be instantiated
    }

    @SuppressWarnings("unchecked")
    static <T> Optional<T> getValue(Object element, Class<T> expectedClass) {
        T result = null;
        if (element instanceof JAXBElement) {
            Object value = ((JAXBElement) element).getValue();
            if (expectedClass.isAssignableFrom(value.getClass())) {
                result = (T) value;
            }
        }

        return Optional.ofNullable(result);
    }

    static <T> Optional<T> getValue(List<Object> elements, Class<T> expectedClass) {
        T result = null;
        for (Object element : elements) {
            Optional<T> optional = getValue(element, expectedClass);
            if (optional.isPresent()) {
                result = optional.get();
                break;
            }
        }

        return Optional.ofNullable(result);
    }
}
