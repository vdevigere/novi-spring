package org.novi.core.activations;
import java.util.HashMap;
import java.util.Map;

public enum FoundActivations {
    REGISTRY(new HashMap<>());
    private final Map<String, BaseActivation> map;
    private FoundActivations(Map<String, BaseActivation> map) {
        this.map = map;
    }

    public Map<String, BaseActivation> getMap() {
        return map;
    }
}
