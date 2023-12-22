package org.novi.core.activations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.novi.core.exceptions.ContextParseException;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseConfiguredActivation<T> {

    private final T configuration;

    public BaseConfiguredActivation(T configuration) {
        this.configuration = configuration;
    }

    public final Boolean evaluateFor(String context) throws ContextParseException {
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
        };
        ObjectMapper mapper = new ObjectMapper();
        try {
            return evaluateFor(mapper.readValue(context, typeRef));
        } catch (JsonProcessingException e) {
            throw new ContextParseException(e);
        }
    }

    public abstract Boolean evaluateFor(Map<String, Object> context);

    public T getConfiguration() {
        return configuration;
    }

}
