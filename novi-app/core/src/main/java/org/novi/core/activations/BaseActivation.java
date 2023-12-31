package org.novi.core.activations;

import org.novi.core.exceptions.ConfigurationParseException;

public interface BaseActivation<T> extends BaseActivationFactory<T> {

    default String getName() {
        return this.getClass().getCanonicalName();
    }

    public abstract Boolean evaluate(String context);

    public abstract T configuration();
}
