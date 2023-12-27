package org.novi.core.activations;

import org.novi.core.exceptions.ConfigurationParseException;

public interface BaseActivation<T> {

    default String getName() {
        return this.getClass().getCanonicalName();
    }

    public abstract BaseActivation<T> valueOf(String configuration) throws ConfigurationParseException;

    public abstract Boolean apply(String context);

    public abstract T configuration();

}
