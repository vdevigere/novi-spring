package org.novi.core.activations;

import org.novi.core.exceptions.ConfigurationParseException;

public interface BaseActivation {

    public String getName();

    public abstract BaseConfiguredActivation<?> whenConfiguredWith(String configuration) throws ConfigurationParseException;
}
