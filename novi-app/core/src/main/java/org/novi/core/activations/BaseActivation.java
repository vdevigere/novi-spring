package org.novi.core.activations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.novi.core.exceptions.ConfigurationParseException;

import java.util.Map;

public interface BaseActivation<T> {

    default String getName() {
        return this.getClass().getCanonicalName();
    }

    public abstract BaseActivation<T> setConfiguration(String configuration) throws ConfigurationParseException;

    public abstract Boolean evaluateFor(Map<String, Object> context);

    default Boolean evaluateFor(String context){
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(context);
            Map<String, Object> contextMap = mapper.treeToValue(root, Map.class);
            return evaluateFor(contextMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract T getConfiguration();

}
