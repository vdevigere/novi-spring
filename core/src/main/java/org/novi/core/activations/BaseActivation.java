package org.novi.core.activations;

import org.novi.core.exceptions.ConfigurationParseException;

public interface BaseActivation {

    default String getName() {
        return this.getClass().getCanonicalName();
    }

    public abstract BaseConfiguredActivation<?> whenConfiguredWith(String configuration) throws ConfigurationParseException;
}
